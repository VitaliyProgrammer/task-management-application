package org.example.infrastructure.specification.field;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.example.domain.entity.Task;
import org.example.infrastructure.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;

public class DueFromSpecificationProvider implements SpecificationProvider<Task> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String getKey() {
        return TaskSpecificationKeys.DUE_FROM;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        LocalDate date = LocalDate.parse(parameters[0], formatter);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), date);
    }
}
