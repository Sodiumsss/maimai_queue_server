## 自己打包启动服务端

1. 更改**src/main/resources**中**application.properties**中内容：

   更改等于号后的xxx、port、abc、qwe、1234以配置数据库链接，数据库账号以及密码。还有服务器端口。

```
   spring.datasource.url=jdbc:mysql://xxx.xxx.xxx.xxx:port/abc
   spring.datasource.username=qwe
   spring.datasource.password=qwe
   server.port=1234
```
注意：/abc为数据库名称，该数据库中需要有表**user_info**，

表中字段：**acc**（非空主键 varchar100）、**pw**(非空varchar100)、**kind**(非空varchar10)

2. 使用**maven**，**package**
   如果成功，jar文件将会在target目录下。
3. 启动方法，用**cmd**输入**java -jar xxxxxx.jar**

## DIY

你可以更改**wb.java**中的**Information**以更新客户端上方公告。







