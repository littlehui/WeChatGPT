## 1. 文档结构

- [x] 1.1 在 `docs/` 下新增项目功能说明主文档（如 `docs/README.md` 或 `docs/project-overview.md`）

## 2. 文档内容

- [x] 2.1 编写项目概述：项目名称（多知堂/dzt）、定位（接入 ChatGPT 的微信公众号助手）
- [x] 2.2 编写技术栈说明：Java 8、Spring Boot 2.5.x、Redis、MyBatis/MyBatis-Plus、微信公众号 SDK、ChatGPT 客户端等
- [x] 2.3 编写模块说明：dzt-web、dzt-service、dzt-dao、dzt-common、dzt-bean、dzt-message 的职责与关系
- [x] 2.4 编写核心功能说明：微信公众号校验与消息处理、ChatGPT 问答流程（含二次确认）、按 OpenID 限流（如 10 秒 4 次）、试用策略（未设置 API Key 时每日 5 次）、上下文条数、用户自定义 API Key（如 /setApiKey）
- [x] 2.5 编写配置与运行说明：Redis/公众号/OpenAI 相关配置（参考 profiles/application-dev.properties）、构建命令（mvn clean package）、启动命令（java -jar 与 spring.profiles.active）

## 3. 可选收尾

- [x] 3.1 在根目录 README.MD 中增加指向 `docs/` 文档的链接（可选）
