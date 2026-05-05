# 项目介绍

接入大语言模型的微信公众号助手；对话能力基于 **Spring AI**，默认对接阿里云 **DashScope 通义千问（Qwen）**。

# 关键实现技术

1. Java:版本17
2. Spring boot:版本：3.2.5
3. Redis:版本4.XX
4. 对话与模型：**Spring AI**（BOM `spring-ai-bom` **1.0.2**）+ **`spring-ai-alibaba-starter-dashscope`（1.0.0.2）**，经 DashScope 调用千问等 Chat 模型
5. 微信公众号客户端:weixin-java-mp，版本：4.4.0

# 功能介绍

1. 接入通义千问等模型（由 `spring.ai.dashscope` 配置），通过互动二次确认问题的方式优化了只能被动推送消息的体验。
2. 支持按照微信OpenID的限流，10秒钟4次。
3. 未设置有效 LLM API Key（当前与配置项 `dev.app.config.openai.apikey` / `prod.app.config.openai.apikey` 共用占位，请填入 **DashScope API Key**）前，每个OpenId限制1天试用5次。设置后无当天的使用次数限制。
4. 支持上下文关联，建议最多3组上下文。
5. 支持设置用户自己的 API Key。在聊天窗口中输入："/setApiKey xxxxxx"，即可。

# 功能演示

1. 正常问答
   ![图片](https://images.lilhui.com/4b788779dd9cbbeed85bebabaa23cfef)

# 如何使用

## 前置准备

1. 公众号已申请。
2. 在基本配置页面获取到 appId,秘钥，token,aesKey。
3. 公众号权限开通接收消息权限。
4. 可访问阿里云 DashScope 的服务器，用来部署后端接口（国内直连通常无需再配 OpenAI 代理；若出网需代理，可对 JVM 配置 `-Dhttps.proxyHost` / `-Dhttps.proxyPort` 等，与运维策略一致即可）。
   配置参考：
   ![图片](https://images.lilhui.com/9a8d26e38aebfb7e5c7ddf915cc3bbc5)

配置注意点：
1. 公众号配置服务器url，这里填写你部署的服务器url, /wechat 是接口名。
2. 先部署接口，然后启用，启用微信会发送一个确认到 /wechat 接口。如果正常则启动成功。
   ![图片](https://images.lilhui.com/b8e3e9cc7be905b9f983d852b5704b35)

## 开始

1. 修改 ./profiles/application-dev.properties配置添加redis和公众号相关配置
```batch
dev.app.redis.host=
dev.app.redis.port=
dev.app.redis.password=

dev.wx.mp.appId=
dev.wx.mp.secret=
dev.wx.mp.token=
dev.wx.mp.aesKey=
```

2. 添加 LLM（DashScope / 通义千问）相关配置：在 `profiles` 中沿用原有属性名写入 **DashScope API Key** 与上下文条数；应用内 `application-dev.yml` / `application-prod.yml` 已通过 `spring.ai.dashscope` 引用这些属性，并默认模型 `qwen-turbo`（可按需改模型名）。
```batch
dev.app.config.openai.apikey=
// 填入 DashScope 控制台申请的 API Key；属性名未改，与历史 OpenAI 配置位共用
dev.app.config.openai.associationRound=
// 以下为可选；主要面向旧版 OpenAI 代理场景，DashScope 直连一般可留空
dev.app.config.openai.proxy=
dev.app.config.openai.proxyPort=
```

`spring.ai.dashscope` 片段示例（实际以仓库内 `dzt-web/dzt-app-web/src/main/resources/application-*.yml` 为准）：
```yaml
spring:
  ai:
    dashscope:
      api-key: ${dev.app.config.openai.apikey}
      chat:
        options:
          model: qwen-turbo
```
3. 编译
```batch
mvn clean package -DskipTests -T 4C
```
编译后在dzt-app-web项目的target包下有一个dzt-app-web.jar包。

4. 启动
```batch
java -jar dzt-app-web.jar --spring.profiles.active=dev
```

# 试用效果
微信公众号搜索："码农星人"并关注,即可试用。
或则扫描二维码：
![图片](https://images.lilhui.com/bacb43240f1ec7cbaace956407b736a1)

