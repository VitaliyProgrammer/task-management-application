package org.example.repository.specification;

import org.example.dto.TaskSearchParameterDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(TaskSearchParameterDto taskSearchParameter);
}
