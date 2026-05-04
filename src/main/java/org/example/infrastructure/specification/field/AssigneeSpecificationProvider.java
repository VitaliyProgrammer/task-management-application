package org.example.infrastructure.specification.field;

import java.util.Arrays;
import org.example.domain.entity.Task;
import org.example.infrastructure.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AssigneeSpecificationProvider implements SpecificationProvider<Task> {
    @Override
    public String getKey() {
        return TaskSpecificationKeys.ASSIGNEE;
    }

    public Specification<Task> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get("assignee").get("id").in(Arrays.stream(parameters)
                        .map(Long::valueOf).toList());
    }
}
