# 项目介绍

接入ChatGPT 3.5的微信公众号助手。

# 关键实现技术

1. Java:版本8
2. Spring boot:版本：2.5.2
3. Redis:版本4.XX
4. chatGPT客户端:chatgpt-java，版本：1.1.5
5. 微信公众号客户端:weixin-java-mp，版本：4.4.0

# 功能介绍

1. 接入ChatGpt 3.5,通过互动二次确认问题的方式优化了只能被动推送消息的体验。
2. 支持按照微信OpenID的限流，10秒钟4次。
3. 未设置openAiApiKey前，每个OpenId限制1天试用5次。设置后无当天的使用次数限制。
4. 支持上下文关联，建议最多3组上下文。
5. 支持设置用户自己的openAiApi。在聊天窗口中输入："/setApiKey xxxxxx"，即可。

# 功能演示

1. 正常问答
   ![图片](https://images.lilhui.com/4b788779dd9cbbeed85bebabaa23cfef)

# 如何使用

## 前置准备

1. 公众号已申请。
2. 在基本配置页面获取到 appId,秘钥，token,aesKey。
3. 公众号权限开通接收消息权限。
4. 可以访openAi的服务器，用来部署后端接口。
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

2. 添加openAiApi配置
```batch
dev.app.config.openai.apikey=
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


