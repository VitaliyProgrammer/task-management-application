package org.example.dto;

public record TaskSearchParameterDto(

        String[] title,
        String[] status,
        String[] priority,
        String[] assignee,
        String[] project,
        String[] createdFrom,
        String[] createdTo,
        String[] dueFrom,
        String[] dueTo,
        String[] hasAttachment
) {
}
