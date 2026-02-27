package com.project.dugoga.domain.ai.repository;

import com.project.dugoga.domain.ai.entity.Ai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID> {

}