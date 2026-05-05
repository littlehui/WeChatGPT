## ADDED Requirements

### Requirement: LLM 对话通过 Spring AI 调用千问（DashScope）

系统 SHALL 使用 Spring AI 提供的对话能力（例如 `ChatModel` 及标准消息类型）向 **阿里云 DashScope 上的千问（通义）模型** 发起聊天补全请求，而 SHALL NOT 在 `ChatServiceImpl` 的对话主路径上依赖 `com.unfbx.chatgpt.OpenAiClient` 完成同类请求。

#### Scenario: 成功返回助手回复

- **WHEN** 用户发起一次有效对话且后端持有有效 DashScope 凭据、模型可用
- **THEN** 系统返回模型生成的助手文本，并 SHALL 将该轮对话按现有规则写入聊天消息缓存（与迁移前行为一致）

#### Scenario: 无用户自备 Key 时计入试用消耗

- **WHEN** 对话请求处理完成且当前用户配置中不存在可用的「LLM / DashScope」用户级 API Key（与现有「无 OpenAI 用户 key」判定等价）
- **THEN** 系统 SHALL 按现有逻辑增加该用户的试用已用次数（与 `freeCountCacheRepository.incrUsedFreeCount` 行为一致）

### Requirement: 关联轮次与上下文过长降级

当模型或网关返回表示 **上下文过长或超出窗口** 的错误时，系统 SHALL 在配置的关联轮次大于 1 时自动 **减少关联轮次** 并重试一次对话；若关联轮次已为 1 或重试仍失败，则 SHALL 返回与现网一致的 **错误占位回答**（例如写入缓存的 `ANSWER_ERROR` 并返回对应常量）。

#### Scenario: 可降级时自动重试

- **WHEN** 聊天补全失败且错误被归类为「上下文过长」且当前关联轮次大于 1
- **THEN** 系统使用关联轮次减一后的参数再次发起补全请求

#### Scenario: 不可降级时返回错误内容

- **WHEN** 聊天补全失败且错误被归类为「上下文过长」且当前关联轮次为 1
- **THEN** 系统 SHALL 将错误回答写入缓存并返回与 `ChatConstants.ANSWER_ERROR` 一致的用户可见结果

### Requirement: 鉴权与超时行为

当凭据无效或无权访问模型时，系统 SHALL 返回与现网 **API Key 错误** 一致的用户可见结果（例如 `ChatConstants.API_KEY_ERROR`）。当对话在配置的处理超时时间内未完成时，系统 SHALL 抛出或返回与现网一致的 **超时语义**（例如 `AnswerTimeOutException`），以便上游重试逻辑不变。

#### Scenario: 无效 Key

- **WHEN** DashScope 返回鉴权失败类错误
- **THEN** 系统返回 API Key 错误常量并 SHALL 将对应错误内容写入回答缓存（与现有 401 分支语义一致）

#### Scenario: 处理超时

- **WHEN** 对话线程在 `ChatConstants.CHAT_PROCESS_TIME_OUT` 秒内未拿到模型结果
- **THEN** 系统行为与迁移前 `Future#get` 超时分支一致（含 `AnswerTimeOutException`）

### Requirement: 自动化测试位于 dzt-app-web

与本变更相关的 **新增或实质性修改的自动化测试**（覆盖 LLM 对话成功、上下文过长降级、鉴权失败、超时等 `specs/llm-chat/spec.md` 所列行为）SHALL 以源代码形式存在于 Maven 模块 **`dzt-web/dzt-app-web`** 的目录 **`src/test/java`** 下，并 SHALL 随本变更可被执行（例如 `mvn -pl dzt-web/dzt-app-web test`）。

#### Scenario: 评审者只在 app-web 测试树中查找测试

- **WHEN** 评审者在本变更范围内查找针对 `ChatServiceImpl`（或等价 LLM 调用路径）的自动化测试
- **THEN** 所有此类测试类的路径均匹配前缀 `dzt-web/dzt-app-web/src/test/java/`

#### Scenario: CI 或本地可运行 app-web 测试

- **WHEN** 开发者在仓库根目录执行针对 `dzt-app-web` 模块的 Maven 测试目标
- **THEN** 上述测试类被编译并执行，且不因测试误放在其它模块的 `src/test/java` 而遗漏
