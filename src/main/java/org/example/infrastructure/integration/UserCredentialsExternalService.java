package org.example.infrastructure.integration;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.User;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserEmailCredentialsService {

    private final UserRepository userRepository;
    private final EmailCredentialsCryptoService credentialsCryptoService;

    @Transactional
    public void setEmailCredentials(Long userId, String emailUsername, String rawAppPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: "
                        + userId));

        String encryptedPassword = credentialsCryptoService.encrypt(rawAppPassword);

        user.setEmailUsername(emailUsername);
        user.setEmailPassword(encryptedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void setTelegramCredentials(Long userId, Long telegramChatId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: "
                        + userId));

        user.setTelegramChatId(telegramChatId);
        userRepository.save(user);
    }
}
