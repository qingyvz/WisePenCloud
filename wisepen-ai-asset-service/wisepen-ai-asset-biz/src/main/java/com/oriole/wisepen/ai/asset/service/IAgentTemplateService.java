package com.oriole.wisepen.ai.asset.service;

import com.oriole.wisepen.ai.asset.domain.dto.req.AgentTemplateUpsertRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateInfoResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateMetaResponse;

import java.util.List;

public interface IAgentTemplateService {

    List<AgentTemplateMetaResponse> listEnabledMetas();

    AgentTemplateInfoResponse getByTemplateId(String templateId);

    void upsert(AgentTemplateUpsertRequest req);
}
