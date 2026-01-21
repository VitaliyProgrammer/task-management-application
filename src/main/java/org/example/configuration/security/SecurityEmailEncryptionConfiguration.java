package org.example.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class SecurityEmailEncryptionConfiguration {

    @Bean
    public TextEncryptor textEncryptor(
            @Value("${security.encryption.secret}") String secret,
            @Value("${security.encryption.salt}") String salt
    ) {
        return Encryptors.text(secret, salt);
    }
}
