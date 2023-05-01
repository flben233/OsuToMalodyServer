# OsuToMalodyServer

一个可以让你在malody v中直接从osu!官方服务器下载osu!mania谱面的程序/服务端，基于Spring Boot 3.0.2编写

## 内容列表

- [使用说明](#使用说明)
- [预构建版本下载](#预构建版本下载)
- [维护者](#维护者)
- [如何贡献](#如何贡献)
- [使用许可](#使用许可)

## 使用说明

### 一般安装

1. 安装`JDK17`并配置好环境变量
2. 下载[预构建版本](https://github.com/flben233OsuToMalodyServer/releases)
3. 解压压缩包
4. 按照以下说明填写`application.yml`
```yaml
server:
  # 连接端口
  port: 8080
malody:
  server:
    api: 202208
    min: 202103
    # 欢迎词
    welcome: Welcome to this osu! to MalodyV server
    # 缓存路径，推荐设置绝对路径，例如 D:\IdeaProjects\osu2malody-bridge\tmp
    tmp: /path/to/your/tmp
    # 本服务端的完整URL(也就是malody连接用的地址)，例如 http://localhost:8080
    url: 
    # 是否将所有谱面全部设置为stable
    showAll: false
    # 是否开启osz文件缓存
    saveTemp: true
  osu:
    # 你的osu用户名
    username: your_username
    # 你的osu密码
    password: your_password
    # HTTP代理，用于加速下载osu谱面
    proxy:
      # 是否开启
      enable: false
      # 服务器地址
      host: 127.0.0.1
      # 服务器端口
      port: 10809
```
5. 运行`start.bat`或`start.sh`启动服务端
6. 在MalodyV中填入本服务端的URL
7. 没了

## 预构建版本下载

[预构建版本](https://github.com/flben233OsuToMalodyServer/releases)

## 维护者

[@flben233](https://github.com/flben233)。

## 如何贡献

非常欢迎你的加入！[提一个 Issue](https://github.com/flben233/OsuToMalodyServer/issues/new) 或者提交一个 Pull Request。

## 使用许可

[MIT](LICENSE) © flben233

