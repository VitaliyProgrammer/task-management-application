package org.example.infrastructure.repository;

import java.util.Optional;
import org.example.domain.entity.Role;
import org.example.domain.entity.status.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);
}
