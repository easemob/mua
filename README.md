# 二次元恋爱互动APP_Mua
##  项目介绍
Mua是一款基于环信IM+声网RTC打造的二次元恋爱互动开源项目,实现一对一单聊在二次元社交领域的新场景。在mua中，不仅能与TA文字语音互动，实时对讲，还能记录心情，完成恋爱清单，拍照留念，养宠物，一起看电影，定制专属开屏等，实时地图再遥远也阻隔不了甜蜜的日常瞬间，生活化的恋爱体验让异地恋不再孤单。

## 功能框架

- 丰富的空间背景交互：电视机、地球仪、照片墙、日历、唱片机、冰箱、复古电话、存钱罐、宠物猫，戳戳都有惊喜

- 浮层组件：用户在线状态、宠物状态、金币值、拍照留念、聊天悬浮窗

- 底部菜单：悄悄话、心情日记、宝箱、商城、个人中心


## 运行项目
开始前，请确保你的开发环境满足如下条件：
Android Studio 4.0.0 或以上版本。
Android 5.0或以上版本的设备。部分模拟机可能无法支持本项目的全部功能，所以推荐使用真机。

### 获取示例项目
前往 GitHub 下载或克隆 [Easemob_mua](https://github.com/easemob/mua/) 示例项目.

### 获取APPKEY
### 1、环信SDK相关
前往[环信官网](https://console.easemob.com/user/register)注册项目，生成APPKey、Client_ID和ClientSecret
  APPKey在APP端，Client_ID和ClientSecret在服务端使用，用户替换时需要一起替换。
  APPKey在AndroidManifest.xml的 第167行 集成用户可以替换为自己的APPKey，
 
 **需要注意的问题**: 
 - 开启消息撤回增值功能、开启用户状态订阅功能。
  - 特别注意：用户开启开放注册功能，同时注意注册用户上限。
  
###  2、声网AgoraAppId
前往 [agora.io](https://sso2.agora.io/cn/v4/signup/with-sms) 注册项目。
  Agora AppId在ImHelper.java 第161行 集成用户可以替换为自己的Agora AppId
### 3、百度SDK相关API_KEY
  API_KEY APPKey在AndroidManifest.xml的93行集成用户可以替换为自己的API_KEY
### 4、腾讯Bugly
  App.java第70行，主要用户APP日志收集用户可以根据自己的实际情况替换或者移除。

  
### 请求地址
  在路径com.community.mua.common里面的Constants文件里面
   HTTP_HOST配置网络请求的地址

## Demo体验
[mua Demo下载体验](https://www.imgeek.org/article/825360597)

![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/1f0f6c9cf365bde4380f239b317d359c.jpeg#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/3c697ae327aebba994e673982bd6f507.jpeg#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/img_convert/91c8f4be289064b05defbbcbf3f32d40.jpeg#pic_center)

## 联系我们
 - 如果你遇到了困难，可以先参阅 [常见问题](https://docs-im.easemob.com/) 
 - 如果你想了解更多官方示例，可以参考
   [官方SDK示例](https://www.easemob.com/download/im)
  - 如果你想了解环信SDK在多个场景下的应用，可以参考
   [官方场景案例](https://www.easemob.com/download/demo)
   - 如果你想了解环信的一些社区开发者维护的项目，可以查看 [社区开源项目](https://www.imgeek.org/code/) 完整的
   - API 文档见 [文档中心](https://docs-im.easemob.com/) 
   - 若遇到问题需要开发者帮助，你可以到
   [开发者社区](https://www.imgeek.org/) 提问 
   - 如果发现了示例代码的 bug，欢迎提交
   [issue](https://github.com/easemob/EasemobVoice/issues)
   
   ## 代码许可
The MIT License (MIT)
