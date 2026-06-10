package com.oriole.wisepen.ai.asset.controller;

import com.oriole.wisepen.ai.asset.domain.dto.req.AgentTemplateUpsertRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateInfoResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.AgentTemplateMetaResponse;
import com.oriole.wisepen.ai.asset.service.IAgentTemplateService;
import com.oriole.wisepen.common.core.domain.R;
import com.oriole.wisepen.common.core.domain.enums.BusinessType;
import com.oriole.wisepen.common.log.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "内部 - 子 Agent 模板", description = "供 chat-service 拉取预制子 Agent 模板元信息与配置,并写入/覆盖模板")
@RestController
@RequestMapping("/internal/agent-template")
@RequiredArgsConstructor
public class InternalAgentTemplateController {

    private final IAgentTemplateService agentTemplateService;

    @Operation(
            summary = "查询启用的子 Agent 模板元信息",
            description = """
                    - 用途：chat-service 的模板 matcher 预热时拉取候选清单,用于关键词召回。
                    - 请求：无参数。
                    - 约束：仅返回 enabled=true 的模板。
                    - 处理：查询启用模板并投影为轻量元信息(templateId/displayName/description/triggers),不含 agentConfig。
                    - 失败：无业务失败项。
                    - 响应：返回启用模板的元信息列表,无启用模板时返回空列表。
                    """
    )
    @GetMapping("/listEnabledMetas")
    public R<List<AgentTemplateMetaResponse>> listEnabledMetas() {
        return R.ok(agentTemplateService.listEnabledMetas());
    }

    @Operation(
            summary = "获取子 Agent 模板详情",
            description = """
                    - 用途：chat-service 执行 create_sub_agent(template_id) 时按 id 取模板完整配置。
                    - 请求：templateId 指定目标模板。
                    - 约束：无。
                    - 处理：按 templateId 查询模板,返回含 agentConfig 与 enabled 的完整信息;agentConfig 为不透明 JSON,本服务不解析其结构。
                    - 失败：模板不存在时不抛业务错误,data 返回 null,由调用方判断。
                    - 响应：返回模板详情;不存在时返回 null。
                    """
    )
    @GetMapping("/getByTemplateId")
    public R<AgentTemplateInfoResponse> getByTemplateId(@RequestParam String templateId) {
        return R.ok(agentTemplateService.getByTemplateId(templateId));
    }

    @Operation(
            summary = "写入或覆盖子 Agent 模板",
            description = """
                    - 用途：种子数据或管理端按 templateId 落库一条模板。
                    - 请求：templateId 为唯一键;displayName/description/triggers/agentConfig 为模板内容;enabled 缺省为 true。
                    - 约束：templateId 非空。
                    - 处理：按 templateId 做 upsert,enabled 为空时补 true;不校验 agentConfig 内部结构。
                    - 失败：无业务失败项。
                    - 响应：成功时返回空结果。
                    """
    )
    @Log(title = "写入子 Agent 模板", businessType = BusinessType.INSERT)
    @PostMapping("/upsert")
    public R<Void> upsert(@RequestBody AgentTemplateUpsertRequest request) {
        agentTemplateService.upsert(request);
        return R.ok();
    }
}
