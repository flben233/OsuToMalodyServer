# OsuToMalodyServer
An application that allow you to directly download osu!mania beatmaps from osu! offcial server in malody v.

Based on Spring Boot 3.0.2

English | [简体中文](https://github.com/flben233/OsuToMalodyServer/blob/master/README_CN.md)

## Content

- [Usage](#Usage)
- [Download](#Download)
- [How to contribute](#How to contribute)
- [Maintainer](#Maintainer)
- [License](#License)

## Usage

### Normally Install

1. Install `JDK17` and configrate your environment variable
2. Download [PreBuild](https://github.com/flben233OsuToMalodyServer/releases)
3. Unzip the package
4. Fill out `application.yml` follow the guidance below
```yaml
server:
  # The port to access
  port: 8080
malody:
  server:
    api: 202208
    min: 202103
    # Welcoming
    welcome: Welcome to this osu! to MalodyV server
    # The path to save temporary files. Absolute path is recommended, such as D:\IdeaProjects\osu2malody-bridge\tmp
    tmp: /path/to/your/tmp
    # Full URL of this server (The URL that malody need to acccess),  such as http://localhost:8080
    url: 
    # If set all beatmaps' status to stable
    showAll: false
  osu:
    # Your osu! username
    username: your_username
    # Your osu! password
    password: your_password
    # HTTP Proxy that use to accelerate the beatmaps downloading in bad network environment
    proxy:
      enable: false
      host: 127.0.0.1
      port: 10809
```
5. Run `start.bat` or `start.sh` to start the server
6. Fill in the full URL of this server in Malody V's beatmap server
7. Enjoy!

## Download

[PreBuild](https://github.com/flben233OsuToMalodyServer/releases)

## Maintainer

[@flben233](https://github.com/flben233)。

## How to contribute

Welcome to your join！[Issue](https://github.com/flben233/OsuToMalodyServer/issues/new) or Pull Request.

## License

[MIT](LICENSE) © flben233

