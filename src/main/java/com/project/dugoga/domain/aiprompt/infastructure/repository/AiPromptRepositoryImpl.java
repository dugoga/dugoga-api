package com.project.dugoga.domain.aiprompt.infastructure.repository;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import com.project.dugoga.domain.aiprompt.domain.repository.AiPromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AiPromptRepositoryImpl implements AiPromptRepository {

    private final AiPromptJpaRepository aiPromptJpaRepository;

    @Override
    public Optional<AiPrompt> findByIdAndDeletedAtIsNull(UUID id) {
        return aiPromptJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<AiPrompt> findByIdAndDeletedAtIsNotNull(UUID id) {
        return aiPromptJpaRepository.findByIdAndDeletedAtIsNotNull(id);
    }

    @Override
    public AiPrompt save(AiPrompt aiPrompt) {
        return aiPromptJpaRepository.save(aiPrompt);
    }
}
