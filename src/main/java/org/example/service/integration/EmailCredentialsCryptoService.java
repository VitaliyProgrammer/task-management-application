package org.example.service.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailCredentialsCryptoService {

    private final TextEncryptor textEncryptor;

    public String encrypt(String rawPassword) {

        return textEncryptor.encrypt(rawPassword);
    }

    public String decrypt(String encryptedPassword) {

        return textEncryptor.decrypt(encryptedPassword);
    }
}
