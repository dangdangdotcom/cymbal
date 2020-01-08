![Cymbal](https://raw.githubusercontent.com/dangdangdotcom/cymbal/master/doc/images/cymbal-logo.png)
# An operational platform for Redis.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Cymbal（铙钹, 一种金属打击乐器）是当当网架构部孵化并开源的Redis PaaS平台，目标是帮助技术团队以简单，低成本的方式管理大规模Redis集群。目前当当网内部使用Cymbal管理的Redis实例数量达到1000+。

Cymbal采用DevOps的设计思想，以多租户的方式，最大程度上赋予开发人员运维权限，从而加快团队运转。同时，Cymbal上面集成了丰富的运维功能：从监控、报警到在线扩缩容等，力求最大程度上消除运维门槛。

Cymbal基于Spring Boot2开发。

# Screenshots

![配置界面](https://raw.githubusercontent.com/dangdangdotcom/cymbal/master/doc/images/cymbal-cluster-screenshot.png)

# Features

* **Redis集群快速部署**
  * 一键部署Redis集群
  * 多版本选择
  * Standalone、cluster多模式选择
  * 集群申请审批工作流

* **主机资源管理**
  * 一键初始化主机环境
  * 主机资源状态展示
  * 资源分配策略（TODO）

* **Redis实例状态监控与报警**
  * 实时状态监控
  * 支持prometheus + alert manager + exporter + grafana的监控方案
  * 丰富的指标监控与报警
  * 报警邮件推送（with alert manger）
  * 集群维度监控展示
  * 全平台维度监控展示

* **Redis常规运维**
  * 启动、停止
  * replication管理
  * 页面客户端
  * 在线扩缩容（水平、垂直）

* **权限管理**
  * 多种安全认证方式（CAS + 常规方式 + LDAP（TODO））
  * 集群访问授权
  
# Keywords

* ![dangdangdotcom](http://img61.ddimg.cn/upload_img/00405/luyi/DDlogoNEW.gif)

* **Redis** ![redis](https://redis.io/images/redis-white.png)

# License
The project is licensed under the [Apache 2 license](https://github.com/ctripcorp/apollo/blob/master/LICENSE).