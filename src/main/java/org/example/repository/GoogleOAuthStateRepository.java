package org.example.repository;

import org.example.entity.GoogleOAuthState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleOAuthStateRepository extends JpaRepository<GoogleOAuthState, String> {
}
