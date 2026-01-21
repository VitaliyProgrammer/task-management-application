package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.TaskSearchParameterDto;
import org.example.entity.Task;
import org.example.repository.field.TaskSpecificationKeys;
import org.example.repository.specification.SpecificationBuilder;
import org.example.repository.specification.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecificationBuilderImpl implements SpecificationBuilder<Task> {

    private final SpecificationProviderManager<Task> taskSpecificationProviderManager;

    @Override
    public Specification<Task> build(TaskSearchParameterDto searchParametersDto) {

        Specification<Task> specification = Specification.where(null);

        if (searchParametersDto.title() != null && searchParametersDto.title().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.TITLE)
                    .getSpecification(searchParametersDto.title()));
        }

        if (searchParametersDto.status() != null && searchParametersDto.status().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.STATUS)
                    .getSpecification(searchParametersDto.status()));
        }

        if (searchParametersDto.priority() != null && searchParametersDto.priority().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.PRIORITY)
                    .getSpecification((searchParametersDto.priority())));
        }

        if (searchParametersDto.assignee() != null && searchParametersDto.assignee().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.ASSIGNEE)
                    .getSpecification((searchParametersDto.assignee())));
        }

        if (searchParametersDto.project() != null && searchParametersDto.project().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.PROJECT)
                    .getSpecification((searchParametersDto.project())));
        }

        if (searchParametersDto.createdFrom() != null
                && searchParametersDto.createdFrom().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.CREATED_FROM)
                    .getSpecification((searchParametersDto.createdFrom())));
        }

        if (searchParametersDto.createdTo() != null && searchParametersDto.createdTo().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.CREATED_TO)
                    .getSpecification((searchParametersDto.createdTo())));
        }

        if (searchParametersDto.dueFrom() != null && searchParametersDto.dueFrom().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.DUE_FROM)
                    .getSpecification((searchParametersDto.dueFrom())));
        }

        if (searchParametersDto.dueTo() != null && searchParametersDto.dueTo().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.DUE_TO)
                    .getSpecification((searchParametersDto.dueTo())));
        }

        if (searchParametersDto.hasAttachment() != null
                && searchParametersDto.hasAttachment().length > 0) {
            specification = specification.and(taskSpecificationProviderManager
                    .getSpecificationProvider(TaskSpecificationKeys.HAS_ATTACHMENT)
                    .getSpecification((searchParametersDto.hasAttachment())));
        }
        return specification;
    }
}
