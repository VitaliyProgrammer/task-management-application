package org.example.infrastructure.specification.field;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.domain.entity.Attachment;
import org.example.domain.entity.Task;
import org.example.infrastructure.specification.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Filters tasks that have (or do not have) at least one attachment,
 * using an EXISTS subquery on the attachments table.
 */
@Component
public class HasAttachmentSpecificationProvider implements SpecificationProvider<Task> {

    @Override
    public String getKey() {
        return TaskSpecificationKeys.HAS_ATTACHMENT;
    }

    @Override
    public Specification<Task> getSpecification(String[] parameters) {

        boolean hasAttachment = Boolean.parseBoolean(parameters[0]);

        return (root, query, criteriaBuilder) -> {

            Subquery<Long> attachments = query.subquery(Long.class);
            Root<Attachment> attachment = attachments.from(Attachment.class);

            attachments.select(attachment.get("id"))
                    .where(criteriaBuilder.equal(attachment.get("task"), root));

            return hasAttachment
                    ? criteriaBuilder.exists(attachments)
                    : criteriaBuilder.not(criteriaBuilder.exists(attachments));
        };
    }
}
