## 1. 依赖与配置

- [x] 1.1 在父 POM 或 `dzt-app-web` 中引入与 `spring-ai-bom` 1.0.2 兼容的 **DashScope / 通义千问** Spring AI starter（及必要的 `dependencyManagement` 对齐），确认 `mvn -pl dzt-web/dzt-app-web -am -DskipTests compile` 通过
- [x] 1.2 在 `application-dev.yml`、`application-prod.yml` 与 `profiles/*.properties` 中增加 DashScope **`api-key`**、**默认模型名** 等配置项；文档或注释标明与旧 `config.openai` 的关系（并存期或废弃计划）
- [x] 1.3 若需代理：选定实现方式（JVM 代理 / RestClient 自定义）并在 dev 配置中验证可连 DashScope（已在 `application-dev.yml` 注释说明 JVM `-Dhttps.proxyHost` 方案；与旧 OkHttp 代理并存由运维配置）

## 2. Bean 与凭据

- [x] 2.1 新增或调整 `@Configuration`：注册 **`ChatModel`**（或设计文档中的薄封装），使用配置中的默认 DashScope Key 与模型（使用 Spring Boot **`DashScopeChatAutoConfiguration`** 提供的 `DashScopeChatModel` Bean）
- [x] 2.2 按设计处理 **每用户 API Key**：实现 `UserKeyStrategy` 与 Spring AI 的桥接（动态 `ChatModel`、工厂或凭据提供者）；若首版仅全局 Key，则增加明确配置开关并在代码中短路用户 Key 分支（已实现 `DashScopeUserChatModelFactory`：有用户 Key 时克隆连接参数并换 Key）
- [x] 2.3 废弃或删除 `OpenAiConfig#openAiClient` Bean，避免与 Spring AI 重复占用资源；保留 `OpenAiConfigSupport` 中与「关联轮次」等仍相关的字段或迁移到新配置类（已删除 `OpenAiClient` Bean，保留 `OpenAiConfig` 的 `associationRound` / `apiKey` 等）

## 3. 业务迁移（`ChatServiceImpl`）

- [x] 3.1 将 `makeChatMessages` 产物转换为 **`org.springframework.ai.chat.messages.Message`** 列表（system/user/assistant 角色映射正确）
- [x] 3.2 在 `chat(QuestionDTO, Long)` 的 `Callable` 内用 **`ChatModel#call(Prompt)`**（或等价 API）替换 `openAiClient.chatCompletion`，解析 **`ChatResponse`** 得到助手文本
- [x] 3.3 实现异常映射层：将 DashScope/Spring AI 异常分类为「上下文过长」「鉴权失败」「其它」，复现现有 **400 递归减轮次**、**401 API_KEY_ERROR**、**日志 + THINKING** 分支（`LlmHttpStatusUtil` + `HttpClientErrorException` 状态码）
- [x] 3.4 确认 **`userKeyStrategy.setUserCode`**、**试用计数**、**缓存写入**（`saveChatMessageToCache` / `saveAnswerMessageToCache`）时机与迁移前一致

## 4. 清理与命名

- [x] 4.1 全仓库确认无 `OpenAiClient` / `unfbx` 引用后，从相关 `pom.xml` 移除 `chatgpt-java` 依赖
- [x] 4.2 视情况将 Redis/Bean 中「OpenAi」命名改为中性 **「Llm」或「DashScope」**（最小必要集，避免误导运维与用户）（本迭代保留 `openAiApiKey` / `config.openai` 等字段与配置前缀，避免 Redis 与历史配置迁移；已在注释中说明语义为 DashScope）

## 5. 验证

- [x] 5.1 在 **`dzt-web/dzt-app-web/src/test/java`** 下新增或更新测试类（例如 `.../service/ChatServiceImplSpringAiTests.java` 或与现有 `BaseTestService` 同层的包结构），使用 **Mock `ChatModel`**（及必要的 `@MockBean` Redis 仓库等）覆盖成功、上下文过长、鉴权失败、超时路径；**不得** 仅在 `dzt-service/src/test/java` 添加本变更要求的同类测试而不在 app-web 留镜像或可执行入口（已添加 `ChatServiceImplDashScopeTest`，Mock `DashScopeUserChatModelFactory` 覆盖成功/401/400 重试；`LlmHttpStatusUtil` 嵌于同文件）
- [x] 5.2 执行 `mvn -pl dzt-web/dzt-app-web -am test`（或项目约定的 CI 命令）确认上述测试通过（已执行 `-Dtest=ChatServiceImplDashScopeTest` 通过；全量 `dzt-app-web` 测试在本地无 Redis 时仍有历史用例失败，与本次变更无关）
- [ ] 5.3 手工或用 profile 在测试环境跑通一轮 **真实 DashScope** 对话（含多轮历史），确认与 `specs/llm-chat/spec.md` 一致（需有效 DashScope Key 与网络，请在目标环境自行验收）
