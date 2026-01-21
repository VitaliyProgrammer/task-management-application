package org.example.repository.field;

import java.util.Arrays;
import org.example.entity.Task;
import org.example.entity.status.TaskStatus;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider implements SpecificationProvider<Task> {
    @Override
    public String getKey() {
        return TaskSpecificationKeys.STATUS;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get("taskStatus")
                        .in(Arrays.stream(parameters)
                                .map(TaskStatus::valueOf)
                                .toList());
    }
}
