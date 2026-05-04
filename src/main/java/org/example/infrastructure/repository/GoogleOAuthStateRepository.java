package org.example.infrastructure.repository;

import org.example.domain.entity.GoogleOAuthState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleOAuthStateRepository extends JpaRepository<GoogleOAuthState, String> {
}
