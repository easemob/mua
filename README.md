## 二次元社交APP—Mua
Mua是一款基于环信IM+声网RTC打造的二次元恋爱互动开源项目,实现一对一单聊在二次元社交领域的新场景。在mua中，不仅能与TA文字语音互动，实时对讲，还能记录心情，完成恋爱清单，拍照留念，养宠物，一起看电影，定制专属开屏等，实时地图再遥远也阻隔不了甜蜜的日常瞬间，生活化的恋爱体验让异地恋不再孤单。


## 第三方SDK集成说明:
### 1、环信SDK相关
- [注册环信Appkey]：(https://console.easemob.com/user/register)
- APPKey、Client_ID和ClientSecret
  APPKey在APP端，Client_ID和ClientSecret在服务端使用，用户替换时需要一起替换。
  APPKey在AndroidManifest.xml的 第167行 集成用户可以替换为自己的APPKey，
  需要注意的问题:开启消息撤回增值功能、开启用户状态订阅功能。
  特别注意：用户开启开放注册功能，同时注意注册用户上限。
### 2、百度SDK相关API_KEY
  API_KEY APPKey在AndroidManifest.xml的93行集成用户可以替换为自己的API_KEY
### 3、腾讯Bugly
  App.java第70行，主要用户APP日志收集用户可以根据自己的实际情况替换或者移除。
### 4、声网AgoraAppId
[注册声网APPID]：(https://sso2.agora.io/cn/v4/signup/with-sms)
  AgoraAppId在ImHelper.java 第161行 集成用户可以替换为自己的AgoraAppId
  
## 请求地址
  在路径com.community.mua.common里面的Constants文件里面
   HTTP_HOST配置网络请求的地址
