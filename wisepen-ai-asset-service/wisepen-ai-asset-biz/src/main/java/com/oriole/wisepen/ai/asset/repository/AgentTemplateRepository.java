package com.oriole.wisepen.ai.asset.repository;

import com.oriole.wisepen.ai.asset.domain.entity.AgentTemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentTemplateRepository extends MongoRepository<AgentTemplateEntity, String> {
    List<AgentTemplateEntity> findByEnabledTrue();
}
