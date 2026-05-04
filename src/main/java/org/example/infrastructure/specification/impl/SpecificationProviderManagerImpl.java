package org.example.infrastructure.specification.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Task;
import org.example.infrastructure.specification.SpecificationProvider;
import org.example.infrastructure.specification.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecificationProviderManagerImpl implements SpecificationProviderManager<Task> {

    private final List<SpecificationProvider<Task>> taskSpecificationProviders;

    @Override
    public SpecificationProvider<Task> getSpecificationProvider(String key) {
        return taskSpecificationProviders.stream()
                .filter(a -> a.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Can`t find specification provider for key "
                                + key));
    }
}
