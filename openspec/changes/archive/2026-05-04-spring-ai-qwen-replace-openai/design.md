## Context

- 工程已使用 **Spring Boot 3.2.5**，父 POM 已声明 **`spring-ai-bom`（`spring-ai.version` = 1.0.2）**，但业务侧对话仍通过 **`com.unfbx.chatgpt.OpenAiClient`** 发起 `chatCompletion`（见 `OpenAiConfig`、`ChatServiceImpl`）。
- 对话流程依赖 **`UserKeyStrategy`** 在请求线程上切换 API Key（用户 Redis 缓存中的 key 与默认 `config.openai.api-key`），并对 **HTTP 400（上下文过长递归缩短轮次）**、**401（密钥错误）**、**超时** 等有明确分支处理。
- 现网可选 **HTTP 代理**（`OpenAiConfig` 内 OkHttp `Proxy`），迁移后需在 Spring AI / 底层客户端上具备等价能力或明确放弃并文档化。

## Goals / Non-Goals

**Goals:**

- 使用 **Spring AI** 的 **`ChatModel`**（或项目内薄封装）调用 **阿里云 DashScope 上的千问（通义）对话模型**，替换 `OpenAiClient` 在 **`ChatServiceImpl#chat`** 路径上的使用。
- 保持对外产品行为：**多轮上下文**、**试用次数**（无用户 key 时递增）、**关联轮次降级重试**、**超时与错误常量**（如 `ChatConstants`）在语义上尽量一致。
- 配置与密钥：默认使用部署配置中的 DashScope API Key；**每用户自定义 Key** 若保留，则该 Key 解释为 **DashScope API Key**（与 proposal 中潜在 BREAKING 一致，可在实现阶段用配置开关控制是否强制）。
- **测试资产位置**：与本变更相关的 **JUnit 测试类**（含 `@SpringBootTest` / `@WebMvcTest` / 纯单元）一律落在 **`dzt-web/dzt-app-web/src/test/java`** 下，包名沿用现有惯例（如 `com.idaymay.dzt.app.web.service`）；`dzt-service` 内实现通过 **测试依赖与 `@Import` / `@MockBean`** 在该模块中驱动，避免在 `dzt-service` 模块重复建 `src/test`。

**Non-Goals:**

- 不在本变更中重做 Redis 领域模型或 WeChat 消息协议（除非为兼容 Key 命名最小改动）。
- 不强制引入流式 SSE 输出（除非现有接口已需要；当前以同步补全为主）。
- 不解决与「OpenAI」品牌相关的历史文案全局重命名（可作为后续独立变更）。

## Decisions

1. **选用 Spring AI 作为唯一 HTTP 抽象层**  
   - **理由**：与现有 BOM 一致，便于单测 Mock `ChatModel`，减少自维护 Retrofit/OkHttp 代码。  
   - **备选**：继续自研 RestClient 调 DashScope HTTP API — 拒绝，重复造轮子且与「引入 Spring Boot AI」目标不符。

2. **DashScope / 千问接入方式**  
   - **首选**：使用与 **Spring AI 1.0.x + Boot 3.2** 兼容的官方或社区 **DashScope（通义）ChatModel Starter**（例如 Alibaba Spring AI 生态中的 DashScope starter，坐标以 Maven Central 可查为准），通过 `application.yml` 配置 `api-key`、`chat.options.model` 等。  
   - **理由**：配置即 Bean，减少手写 `ClientHttpConnector`。若 starter 版本与 `spring-ai-bom` 1.0.2 存在传递冲突，则在父 POM 用 `dependencyManagement` 对齐版本并记录。

3. **`ChatServiceImpl` 改造形态**  
   - 注入 **`ChatModel`**（或包装类型 `LlmChatClient` 仅暴露 `String complete(List<Message>)`），在 `Callable` 内将现有 `List<com.unfbx...Message>` 转为 **`org.springframework.ai.chat.messages.*`**（`UserMessage` / `AssistantMessage` / `SystemMessage`），调用 **`ChatResponse`** 取第一条 assistant 文本。  
   - **理由**：隔离 Spring AI 类型与历史 DTO，便于渐进删除 `unfbx` 依赖。

4. **每用户 API Key**  
   - **决策**：保留 `UserKeyStrategy` 的「线程级选 Key」思路，改为在调用前构造 **带动态 API Key 的 `ChatModel`** 或使用 Spring AI 支持的 **运行时凭据提供者**（若 starter 支持）；若 API 不支持，则使用 **DashScope OpenAPI 兼容模式** 或 **工厂每次创建短生命周期 Client**（需注意连接池成本，可接受时再优化）。  
   - **备选**：首版仅支持全局 Key — 标为 **Open Question**，若产品要求必须保留用户 Key 则按首选实现。

5. **异常映射**  
   - 将 Spring AI / DashScope 客户端抛出的异常（如 4xx/5xx、鉴权失败）映射到现有 **`HttpException` 分支等价的语义**：上下文过长 → 触发 `associationRound - 1` 重试；鉴权 → `API_KEY_ERROR`；其它 → 日志 + `THINKING` 或现有默认。  
   - **实现提示**：封装一层 `try/catch`，根据 HTTP 状态或错误码字符串分类，避免在业务方法中散落 SDK 类型。

## Risks / Trade-offs

- **[Risk] Spring AI 与 DashScope starter 版本矩阵不兼容** → **缓解**：在 `dzt-app-web` 上跑通 `mvn -pl dzt-web/dzt-app-web -am test`，锁定 BOM 与 starter 文档推荐组合。  
- **[Risk] 每用户 Key 在 Spring AI Bean 模型下实现复杂** → **缓解**：首迭代可配置「仅全局 Key」上线，用户 Key 作为第二阶段；或在设计评审后选用「请求级 Client」。  
- **[Risk] 400「上下文过长」与 OpenAI 错误体不一致** → **缓解**：对 DashScope 返回 message / code 做白名单匹配，无法识别时降级为通用错误而非无限重试。  
- **[Trade-off] 移除 `unfbx`** 后失去对 OpenAI 官方端点的零适配 — 若仍需 OpenAI，应通过 Spring AI 的 **OpenAI ChatModel** 双 Bean（非本 proposal 首版目标）。

## Migration Plan

1. 在 **dev/prod** 配置中增加 DashScope API Key 与模型名；保留原 `config.openai` 直至切流完成（或 feature flag）。  
2. 实现 `ChatModel` Bean 与 `ChatServiceImpl` 切换；在测试环境对 **有/无用户 Key**、**长上下文**、**错误 Key** 做手工或集成测试。  
3. 确认无其它模块引用 `OpenAiClient` 后，从 `pom` 移除 `chatgpt-java` 相关依赖，删除或收缩 `OpenAiConfig#openAiClient` Bean。  
4. **回滚**：保留 Git revert 与旧依赖坐标；配置层可一键指回旧实现（若采用 flag）。

## Open Questions

- 产品是否要求 **首版即支持用户自备 DashScope Key**（沿用 `/setApiKey` 语义），还是可先 **仅平台 Key**？  
- 代理是否仍为 **硬需求**；若是，选定 starter 后明确由 **JVM 代理**、**Spring `RestClient` 代理** 还是 **侧车** 实现。  
- 默认千问 **模型名**（如 `qwen-turbo` / `qwen-plus`）以运维环境为准，是否在代码中写死默认值。
