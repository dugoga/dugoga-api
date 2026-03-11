package com.project.dugoga.domain.aiprompt.domain.repository;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;

import java.util.Optional;
import java.util.UUID;

public interface AiPromptRepository {

    Optional<AiPrompt> findByIdAndDeletedAtIsNull(UUID id);
    Optional<AiPrompt> findByIdAndDeletedAtIsNotNull(UUID id);

    AiPrompt save(AiPrompt aiPrompt);
}
