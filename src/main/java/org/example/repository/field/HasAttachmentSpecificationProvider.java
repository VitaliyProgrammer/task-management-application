package org.example.repository.field;

import org.example.entity.Task;
import org.example.repository.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class HasAttachmentSpecificationProvider implements SpecificationProvider<Task> {

    @Override
    public String getKey() {
        return TaskSpecificationKeys.HAS_ATTACHMENT;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        boolean hasAttachment = Boolean.parseBoolean(parameters[0]);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hasAttachment"), hasAttachment);
    }
}
