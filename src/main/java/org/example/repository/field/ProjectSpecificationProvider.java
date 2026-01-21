package org.example.repository.field;

import java.util.Arrays;
import org.example.entity.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectSpecificationProvider implements SpecificationProvider<Task> {
    @Override
    public String getKey() {
        return TaskSpecificationKeys.PROJECT;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get("project").get("id")
                        .in(Arrays.stream(parameters)
                                .map(Long::valueOf)
                                .toList());
    }
}
