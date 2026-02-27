package com.project.dugoga.domain.aiPrompt.repository;

import com.project.dugoga.domain.aiPrompt.entity.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiPromptRepository extends JpaRepository<AiPrompt, UUID> {

}
