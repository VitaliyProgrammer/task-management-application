package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.LabelCreateRequestDto;
import org.example.dto.LabelResponseDto;
import org.example.service.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@Tag(name = "Labels", description = "Endpoints for managing tasks labels")
public class LabelController {

    private final LabelService labelService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new label",
            description = "Creates a new label that can be assigned to tasks. "
                    + "Label names must be unique.")
    public LabelResponseDto create(@RequestBody @Valid LabelCreateRequestDto request) {
        return labelService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all labels",
            description = "Returns a paginated list of all  available labels.")
    public Page<LabelResponseDto> getAllLabels(Pageable pageable) {
        return labelService.findAll(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update label",
            description = "Updates the name or properties of an existing label.")
    public LabelResponseDto update(@PathVariable Long id,
                                   @RequestBody @Valid LabelCreateRequestDto request) {
        return labelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete label",
            description = "Deletes a label by its ID. "
                    + "Only administrators are allowed to perform this operation.")
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
