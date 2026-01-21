package org.example.repository.field;

import org.example.entity.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Task> {

    @Override
    public String getKey() {
        return TaskSpecificationKeys.TITLE;
    }

    public Specification<Task> getSpecification(String[] parameters) {
        return (root, query, criteriaBuilder) ->
                root.get("title").in((Object[]) parameters);
    }
}
