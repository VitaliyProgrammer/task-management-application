package org.example.infrastructure.specification.field;

import org.example.domain.entity.Task;
import org.example.infrastructure.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Case-insensitive "title contains" search: "kanban" finds "Kanban board view".
 */
@Component
public class TitleSpecificationProvider implements SpecificationProvider<Task> {

    @Override
    public String getKey() {
        return TaskSpecificationKeys.TITLE;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        String pattern = "%" + parameters[0].trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
    }
}
