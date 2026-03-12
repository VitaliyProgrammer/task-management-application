package org.example.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.User;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found!: "
                                + email));

        return new UserSecurity(user);
    }
}
