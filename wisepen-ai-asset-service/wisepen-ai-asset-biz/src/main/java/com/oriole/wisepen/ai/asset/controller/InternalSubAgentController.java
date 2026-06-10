package com.oriole.wisepen.ai.asset.controller;

import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentConfigPutRequest;
import com.oriole.wisepen.ai.asset.domain.dto.req.SubAgentPriorTurnAppendRequest;
import com.oriole.wisepen.ai.asset.domain.dto.res.PriorTurnResponse;
import com.oriole.wisepen.ai.asset.domain.dto.res.SubAgentConfigResponse;
import com.oriole.wisepen.ai.asset.service.ISubAgentStateService;
import com.oriole.wisepen.common.core.domain.R;
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

// putConfig / appendPriorTurn 是一轮对话内的高频临时态写,不做 @Log 审计落库(白名单),链路追踪由 OT span 覆盖
@Tag(name = "内部 - 子 Agent 运行态", description = "供 chat-service 读写子 Agent 运行态:config 与跨调用 prior_turns,均为 24h TTL 临时数据")
@RestController
@RequestMapping("/internal/sub-agent")
@RequiredArgsConstructor
public class InternalSubAgentController {

    private final ISubAgentStateService subAgentStateService;

    @Operation(
            summary = "写入子 Agent 运行态配置",
            description = """
                    - 用途：create_sub_agent 注册一个子 Agent 实例的运行态配置。
                    - 请求：sessionId + agentId 定位实例;displayName 为展示名;agentConfig 为不透明配置 JSON。
                    - 约束：无。
                    - 处理：以 sessionId:agentId 为键写入 Redis 并设 24h TTL;不解析 agentConfig 结构。
                    - 失败：无业务失败项。
                    - 响应：成功时返回空结果。
                    """
    )
    @PostMapping("/putConfig")
    public R<Void> putConfig(@RequestBody SubAgentConfigPutRequest request) {
        subAgentStateService.putConfig(request);
        return R.ok();
    }

    @Operation(
            summary = "获取子 Agent 运行态配置",
            description = """
                    - 用途：call_sub_agent 调用前取回该子 Agent 的 displayName 与 agentConfig。
                    - 请求：sessionId + agentId 定位实例。
                    - 约束：无。
                    - 处理：按键读 Redis;agentConfig 为不透明 JSON,原样返回。
                    - 失败：配置不存在或已过期时不抛业务错误,data 返回 null,由调用方判断。
                    - 响应：返回 displayName 与 agentConfig;不存在时返回 null。
                    """
    )
    @GetMapping("/getConfig")
    public R<SubAgentConfigResponse> getConfig(@RequestParam String sessionId, @RequestParam String agentId) {
        return R.ok(subAgentStateService.getConfig(sessionId, agentId));
    }

    @Operation(
            summary = "追加子 Agent 跨调用历史",
            description = """
                    - 用途：call_sub_agent 完成后记录一轮 (query, result),供同一 agentId 后续调用作上下文。
                    - 请求：sessionId + agentId 定位实例;query 与 result 为本轮内容。
                    - 约束：无。
                    - 处理：RPUSH 进 Redis List,LTRIM 保留最近 N 条滑窗,并刷新 24h TTL。
                    - 失败：无业务失败项。
                    - 响应：成功时返回空结果。
                    """
    )
    @PostMapping("/appendPriorTurn")
    public R<Void> appendPriorTurn(@RequestBody SubAgentPriorTurnAppendRequest request) {
        subAgentStateService.appendPriorTurn(request);
        return R.ok();
    }

    @Operation(
            summary = "查询子 Agent 跨调用历史",
            description = """
                    - 用途：call_sub_agent 调用前取回同一 agentId 的历史 (query, result) 对,拼接为子 Agent 上下文。
                    - 请求：sessionId + agentId 定位实例。
                    - 约束：无。
                    - 处理：LRANGE 读取整个历史 List,按插入顺序返回。
                    - 失败：无业务失败项。
                    - 响应：返回历史轮次列表,无历史时返回空列表。
                    """
    )
    @GetMapping("/listPriorTurns")
    public R<List<PriorTurnResponse>> listPriorTurns(@RequestParam String sessionId, @RequestParam String agentId) {
        return R.ok(subAgentStateService.listPriorTurns(sessionId, agentId));
    }
}
