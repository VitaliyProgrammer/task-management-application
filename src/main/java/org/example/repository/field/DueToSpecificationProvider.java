package org.example.repository.field;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.example.entity.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DueToSpecificationProvider implements SpecificationProvider<Task> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String getKey() {
        return TaskSpecificationKeys.DUE_TO;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        LocalDate date = LocalDate.parse(parameters[0], formatter);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), date);
    }
}
