# Deploying monitor service

Cymbal支持两种监控方案：

1. 内置监控
2. Prometheus + Grafana + redis exporter + node exporter (TODO) 的监控方案

其中模式1部署更为简单，但监控功能不如模式2强大。

我们推荐使用Cymbal提供的Docker Compose方案部署模式2下的Cymbal服务（TODO）

本文主要介绍方案2的各服务部署及配置方法

## 部署及配置

使用监控方案2，还需部署以下服务：

1. [Prometheus - Alertmanager](https://prometheus.io/docs/alerting/alertmanager/)
2. [Prometheus](https://prometheus.io/)
3. [Grafana](http://grafana.org/)

### Prometheus - Alertmanager

#### 部署 Alertmanager

1. [下载Prometheus](https://prometheus.io/download/)
2. 将以下文件拷贝到Alertmanager的部署文件夹内
    1. `cymbal/src/prometheus/alertmanager/alertmanager.yml`

#### 启动 Alertmanager

```
nohup ./alertmanager --config.file=alertmanager.yml >alertmanager.log 2>&1 &
```

#### 修改 Alertmanager 配置

更多配置信息也可查看[Alertmanager官方文档](https://prometheus.io/docs/alerting/alertmanager/)。

##### Cymbal 服务地址

将alertmanager.yml中第18行的cymbal.io改为实际Cymbal服务地址：

```
url: 'http://cymbal.io/api/v1/alertmanager/webhook'
```

##### Alertmanager 监听端口

默认端口为9093，也可以通过在启动命令中增加 `--web.listen-address=":9093"` 修改。

此端口需要配置到Prometheus配置文件中。

### Prometheus

#### 部署 Prometheus

1. [下载Prometheus](https://prometheus.io/download/)
2. 将以下文件拷贝到Prometheus的部署文件夹内
    1. cymbal/src/prometheus/prometheus.yml
    2. cymbal/src/prometheus/rules.yml
3. 在Prometheus部署文件夹内建立conf.d文件夹

#### 启动 Prometheus

```
prometheus --config.file=prometheus.yml --storage.tsdb.retention=14d --web.enable-lifecycle --web.external-url=prometheus
```

#### 修改 Prometheus 配置

##### Prometheus服务端口

默认为9090端口，也可通过在启动命令中增加以下内容修改：

```
--web.listen-address="0.0.0.0:9090"
```

##### Alertmanager 服务地址

将prometheus.yml中的第12行的alertmanager.cymbal.io修改为Alertmanger实际的服务地址：

```
- alertmanager.cymbal.io
```

### Grafana

#### 部署 Grafana

[从官网下载Grafana并按步骤安装](https://grafana.com/grafana/download)

#### 修改配置

1. (重要) `root_url`增加`/grafana/`
   1. `root_url = %(protocol)s://%(domain)s:%(http_port)s/grafana/`
2. 修改监听端口
   1. `http_port = 9091`

#### 启动 Grafana

[参考官方文档: 启动Grafana](https://grafana.com/docs/grafana/latest/installation/rpm/)

#### 配置Prometheus数据源

[参考官方文档：配置数据源](https://grafana.com/docs/grafana/latest/guides/getting_started/#how-to-add-a-data-source)

#### 导入dashboard

将以下dashboard模板导入Grafana，并按引导完成必要配置：

1. `cymbal/src/dashboard/redis-cluster.json`
2. `cymbal/src/dashboard/redis-cluster-total.json`
3. `cymbal/src/dashboard/redis-statistic.json`

#### 将dashboard id复制到指定页面中

##### 页面路径

```
symbal/cymbal-portal/src/main/resources/templates/redis/tab/redis_monitor_grafana.ftl
```

##### 修改方法

在页面中找到如下代码段，修改${dashborad_id}为真实的grafana dashboard id即可。

```html
<iframe id="redis_monitor_grafana_body" style="position: relative; left: -80px; overflow: hidden;" width="100%" height="1600px" frameborder="0" scrolling="no" url="/grafana/d/${dashborad_id}?var-CLUSTER_ID=${cluster.clusterId}" onload="grafanaPageLoaded()">
```
