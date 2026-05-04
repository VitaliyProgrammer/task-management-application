package org.example.infrastructure.repository;

import org.example.domain.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {

    boolean existsByName(String name);
}
