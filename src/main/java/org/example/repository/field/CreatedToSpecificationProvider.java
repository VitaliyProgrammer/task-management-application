package org.example.repository.field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.example.entity.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;

public class CreatedToSpecificationProvider implements SpecificationProvider<Task> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String getKey() {
        return TaskSpecificationKeys.CREATED_TO;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        LocalDate date = LocalDate.parse(parameters[0], formatter);
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endOfDay);
    }
}
