package com.oriole.wisepen.ai.asset.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.oriole.wisepen.ai.asset.domain.dto.req.AgentTemplateUpsertRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateInfoResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateMetaResponse;
import com.oriole.wisepen.ai.asset.domain.entity.AgentTemplateEntity;
import com.oriole.wisepen.ai.asset.repository.AgentTemplateRepository;
import com.oriole.wisepen.ai.asset.service.IAgentTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentTemplateServiceImpl implements IAgentTemplateService {

    private final AgentTemplateRepository agentTemplateRepository;

    @Override
    public List<AgentTemplateMetaResponse> listEnabledMetas() {
        return agentTemplateRepository.findByEnabledTrue()
                .stream()
                .map(entity -> BeanUtil.copyProperties(entity, AgentTemplateMetaResponse.class))
                .toList();
    }

    @Override
    public AgentTemplateInfoResponse getByTemplateId(String templateId) {
        return agentTemplateRepository.findById(templateId)
                .map(entity -> BeanUtil.copyProperties(entity, AgentTemplateInfoResponse.class))
                .orElse(null);
    }

    @Override
    public void upsert(AgentTemplateUpsertRequest req) {
        AgentTemplateEntity entity = BeanUtil.copyProperties(req, AgentTemplateEntity.class);
        if (entity.getEnabled() == null) entity.setEnabled(true);
        agentTemplateRepository.save(entity);
    }
}
