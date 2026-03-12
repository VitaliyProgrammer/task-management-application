package org.example.infrastructure.specification;

import org.example.presentation.dto.request.TaskSearchParameterDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(TaskSearchParameterDto taskSearchParameter);
}
