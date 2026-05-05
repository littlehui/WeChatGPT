## Why

当前对话能力依赖第三方 `unfbx/chatgpt` 的 `OpenAiClient` 直连 OpenAI 兼容接口，与 Spring 生态耦合较弱，也不利于统一采用国内可用的千问（通义）模型。引入 **Spring AI** 并以千问作为默认/主要 Chat 模型，可在保持 Spring Boot 惯用配置与可测试性的前提下完成模型提供方切换。

## What Changes

- 引入 **Spring AI** 相关依赖（含阿里云 DashScope / 千问 Chat 模型适配），移除或逐步弃用 `OpenAiClient`（`com.unfbx.chatgpt`）在业务路径上的使用。
- 将 `ChatServiceImpl` 中的 `chatCompletion` 调用改为通过 Spring AI 的 `ChatModel`（或等价抽象）调用千问。
- 调整应用配置：`application*.yml` / `profiles` 中增加 Spring AI / DashScope（API Key、模型名、base URL 如有）配置；与现有 `config.openai` 并存或迁移策略在设计中明确。
- **BREAKING（潜在）**：若完全移除 OpenAI 兼容路径，用户自备 OpenAI Key 的行为可能需改为「DashScope API Key」或双模型策略；具体是否在首版保留双后端由设计文档与任务锁定。
- 为本变更补充 **单元测试（及必要的 Spring 切片/集成测）**，代码 **仅** 放在模块 `dzt-web/dzt-app-web` 的 `src/test/java` 下（与现有 `AppIndexControllerTests` 等一致），通过 Mock `ChatModel` 等验证对话与异常分支。

## Capabilities

### New Capabilities

- `llm-chat`: 通过 Spring AI 调用千问（DashScope）完成多轮对话补全，替代原先基于 `OpenAiClient` 的聊天请求；包含超时、错误分类（鉴权、上下文过长等）与试用计数触发条件在行为上尽量与现有一致。

### Modified Capabilities

- （无）仓库根目录 `openspec/specs/` 下尚无已发布能力规格；本变更为首次为 LLM 对话行为立约。

## Impact

- **模块**：`dzt-web/dzt-app-web`（配置与 Bean）、`dzt-service`（`ChatServiceImpl` 及可能抽取的 LLM 门面）、根与各子模块 `pom.xml`。
- **依赖**：新增 `spring-ai-*`（及 DashScope starter 或 BOM）；可能减少 `chatgpt-java` 相关依赖。
- **配置与运维**：需 DashScope（或兼容）API Key；代理（`OpenAiConfig` 中 OkHttp 代理）若仍需要，需在 Spring AI / 底层 HTTP 客户端层对齐。
- **周边**：`UserKeyStrategy`、`OpenAiConfigSupport`、用户维度 API Key 缓存与限流（如 `RateLimiterAspect`）若仍绑定「OpenAI Key」命名，需重命名或语义扩展为「LLM API Key」以避免误导。
- **测试**：新增/调整的自动化测试 **必须** 位于 `dzt-web/dzt-app-web/src/test/java`（不在 `dzt-service` 等模块另起测试树，除非后续变更明确拆分）。
