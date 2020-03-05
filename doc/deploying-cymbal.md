# Deploying cymbal

* <a href="#1">准备工作</a>
* <a href="#2">安装步骤</a>
* <a href="#3">配置信息</a>
* <a href="#4">启动Cymbal</a>

Cymbal秉承开箱即用的原则，整个部署过程十分简单，最小化版本只需要一个runnable jar及mysql服务的支持即可。

同时，我们会在后续迭代中尽量提供各类丰富的一键式运维脚本，帮助使用者消灭运维门槛。

## 运行时环境

Cymbal共支持以下几种运行时环境：

1. Linux CentOS 7及以上
2. Docker (TODO)

## 工作模式

Cymbal根据监控方式的不同，共分为两种工作模式：

1. 内置监控
2. Prometheus + Grafana + redis exporter + node exporter (TODO) 的监控方案

其中模式1部署更为简单，但监控功能不如模式2强大。

我们推荐使用Cymbal提供的Docker Compose方案部署模式2下的Cymbal服务（TODO）。

## <a name="1">准备工作</a>

### 1.1 运行时环境

#### 1.1.1 OS

Linux CentOS 7+

#### 1.1.2 Java

1.8+

可在OS中使用以下命令查看Java环境版本号：

```
java -version
```
返回内容样例：
```
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
```

#### 1.1.3 数据库

目前仅支持MySQL，无版本要求

### 1.2 运行环境

目前Cymbal共支持以下几种运行环境，可在maven编译时按需选择：

* DEV 开发环境
* FAT 测试环境
* PROD 生产环境

## <a name="2">安装步骤</a>

目前alpha版本的安装步骤主要分为以下几步:

1. MySQL数据库初始化
2. 编译Cymbal并获取runnable jar
3. 拷贝必要的文件至部署路径
4. 部署工作模式2所需的其他必要服务

### 2.1 MySQL数据库初始化

#### 2.1.1 创建数据表并插入初始化数据

1. 在MySQL中建立名为`cymbal`的database
2. 执行 cymbal-service/src/resources/datasource/schema.sql 构建数据库表
    1. 如：mysql –u${mysql_user} –p${mysql_password} < schema.sql
3. 执行 cymbal-service/src/resources/datasource/data.sql 构建初始化数据
    1. 如：mysql –u${mysql_user} –p${mysql_password} < data.sql

#### 2.1.2 验证结果

可通过以下SQL语句来验证2.1.1的操作结果

```sql
select id, user_name, user_cn_name, email from user limit 1;
```

### 2.2 编译Cymbal

#### 2.2.1 下载Cymbal至编译环境

在目标路径执行以下命令：

```
git clone https://github.com/dangdangdotcom/cymbal.git
```

#### 2.2.2 使用maven编译Cymbal

在2.2.1下载的Cymbal文件夹内执行以下命令：

```
mvn clean install -P prod -D maven.test.skip=true
```

出现`BUILD SUCCESS`字样后，在cymbal-portal/target/路径下可以找到cymbal.jar。

### 2.3 拷贝必要的文件至部署路径

#### 2.3.1 建立部署用户

建议在部署的操作系统上为cymbal建立专属用户，以实现权限隔离。可用以下命令实现：

```
useradd -d /home/cymbal cymbal
```

然后修改该用户的登陆密码：

```
passwd cymbal
```

#### 2.3.2 拷贝必要的文件

请将Cymbal项目中以下文件、文件夹拷贝至目标部署路径：

1. cymbal/src/redis-portal
2. cymbal/src/ansible
3. cymbal/src/shell/download_redis.sh
    1. 并执行`source download_redis.sh`
4. cymbal/src/redis_exporter/redis_exporter.tar.gz
    1. 拷贝至software_package/路径下
5. 2.2.2中编译出的cymbal.jar

所以，以部署路径为/home/cymbal/为例，文件结构如下：

```
- home
  - cymbal
     - ansible
     - redis-portal
     - software-package
     - cymbal.jar
     - application.yml
``` 

## <a name="3">配置信息</a>

参考文档：[Cymbal的配置](https://github.com/dangdangdotcom/cymbal/wiki/Configurations-of-Cymbal)

## <a name="4">启动Cymbal</a>

### 启动命令

启动命令：

```
java -jar cymbal.jar
```

## 高级监控方案
参考文档：[Cymbal高级监控方案的部署及配置](https://github.com/dangdangdotcom/cymbal/wiki/Deploying-monitor-service)