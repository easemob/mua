#Java服务端代码使用概要：
 ##1、IM相关    
    1、com.easemob.mua.utils目录下ImHttp文件是配置IM请求的 APPKEY、ClientID、ClientSecret 用户去环信后台申请并替换为自己的相关值；
 ##2、数据库配置
    application.yml文件的第7--9行 配置数据库相关
    url: jdbc:mysql://ip:port/数据库名?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=true
    username: 账号
    password: 密码
 ##3、其它相关配置
    application.properties文件
    1、第3行  server.port=port 配置访问的端口号
    2、第28--37行  配置文件存储目录用户可以根据自己情况在指定目录创建
    pic.dir = /work/mua/pic/
    headimg.dir = /work/mua/headimg/
    album.dir = /work/mua/album/
    diaryPic.dir = /work/mua/diaryPic/