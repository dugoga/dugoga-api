package com.project.dugoga.domain.aiprompt.infastructure.repository;

import com.project.dugoga.domain.aiprompt.domain.model.entity.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiPromptJpaRepository extends JpaRepository<AiPrompt, UUID> {

    Optional<AiPrompt> findByIdAndDeletedAtIsNull(UUID id);
    Optional<AiPrompt> findByIdAndDeletedAtIsNotNull(UUID id);
}
