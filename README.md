## 第三方SDK集成说明:
### 1、环信SDK相关 
  注册环信账号，并生成APPKey、Client_ID和ClientSecret
  APPKey在APP端，Client_ID和ClientSecret在服务端使用，开发者使用源码运行时需要一起替换。
  APPKey在AndroidManifest.xml的 第167行 集成用户可以替换为自己的APPKey，
  需要注意的问题:开启消息撤回增值功能、开启用户状态订阅功能。
  特别注意：用户开启开放注册功能，同时注意注册用户上限。
  
### 2、声网AgoraAppId
  AgoraAppId在ImHelper.java 第161行 集成用户可以替换为自己的AgoraAppId，
  
### 3、百度SDK相关API_KEY
  API_KEY APPKey在AndroidManifest.xml的93行集成用户可以替换为自己的API_KEY
  
### 4、腾讯Bugly
  App.java第70行，主要用户APP日志收集用户可以根据自己的实际情况替换或者移除。







