# Configurations of Cymbal

Cymbal基于Spring-Boot开发，因此支持Spring-Boot所支持的一切配置方式，例如：

1. 通过配置文件
    * `./application.yml`
    * `./config/application.yml`
2. 通过启动命令
    * 启动命令后追加 `--item=value`
    * 配置项支持多个，以空格分割

本文以配置文件的方式，介绍Cymbal的配置项信息。

## 配置项说明

### 配置文件模板

可将以下配置文件作为模板，并稍作修改：

`cymbal/src/config/application.yml`

### 特别说明

#### 监控类型

Cymbal支持两种监控方式，通过`monitor.type`配置项来配置，可以支持以下两种类型：

1. default
    * 使用Cymbal内置监控模块
2. grafana
    * 使用Grafana + Prometheus的监控方案，监控指标比default类型更加全面，但需要额外部署一些三方服务，具体请参考[部署监控服务](https://github.com/dangdangdotcom/cymbal/wiki/Deploying-monitor-service)
    