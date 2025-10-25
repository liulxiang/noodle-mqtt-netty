# Noodle MQTT Server

<p align="center">
  <img src="https://img.shields.io/badge/Java-8-blue.svg" alt="Java 8">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.2-brightgreen.svg" alt="Spring Boot 2.7.2">
  <img src="https://img.shields.io/badge/Netty-4.1.128.Final-orange.svg" alt="Netty 4.1.128.Final">
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License">
</p>

## 📖 简介

Noodle MQTT Server 是一个基于 Spring Boot 2.7.2 和 Netty 4.1.128.Final 实现的高性能 MQTT 服务端，支持完整的 MQTT 3.1.1 协议特性。该项目专注于提供一个纯净、轻量级的 MQTT 服务解决方案，适合学习 MQTT 协议或作为生产项目中的 MQTT 服务集成。

### 🌟 核心特性

- ✅ 完整的 MQTT 3.1.1 协议支持
- ✅ 同时支持 MQTT 和 WebSocket 双协议
- ✅ QoS 0/1/2 三种消息质量等级
- ✅ 客户端身份认证（账号密码 + Client ID 白名单）
- ✅ 主题一致性校验
- ✅ 无 Web 依赖设计，专注 MQTT 核心功能
- ✅ 灵活的配置选项
- ✅ 完善的日志系统

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **核心框架** | Spring Boot 2.7.2 | Spring Boot 2.x 系列最终稳定版本 |
| **网络通信** | Netty 4.1.128.Final | Netty 4.1 系列最新稳定版本 |
| **开发语言** | Java 8 | - |
| **构建工具** | Maven 3.9+ | - |
| **JSON处理** | Fastjson 1.2.56 | - |
| **工具类库** | Apache Commons Lang3 | - |
| **代码简化** | Lombok | - |
| **MQTT客户端** | Eclipse Paho MQTT v3 1.2.5 | 用于测试 |

## 📁 项目结构

```
src/main/java/com/noodle/netty/
├── config/                  # 配置类
│   ├── MqttProperties.java     # MQTT配置属性
│   └── WebsocketProperties.java # WebSocket配置属性
├── core/                    # 核心模块
│   ├── cache/              # 缓存接口与实现
│   ├── dto/                # 数据传输对象
│   ├── enums/              # 枚举类
│   ├── server/             # 服务器抽象基类
│   └── service/            # 业务服务抽象
├── mqtt/                   # MQTT协议实现
│   ├── handler/            # MQTT消息处理器
│   ├── service/            # MQTT业务服务
│   ├── MqttServer.java     # MQTT服务器实现
│   ├── MyMqttDecoder.java  # MQTT解码器
│   └── MyMqttCodecUtil.java # MQTT编解码工具
├── websocket/              # WebSocket协议实现
│   ├── handler/            # WebSocket消息处理器
│   ├── service/            # WebSocket业务服务
│   └── WebSocketServer.java # WebSocket服务器实现
├── NettyServerInit.java    # Netty服务初始化
└── MainApplication.java    # Spring Boot主启动类

src/main/resources/
├── application.yml         # 应用配置文件
└── logback-spring.xml      # 日志配置文件

src/test/java/com/my/
└── ConnectionTest.java     # MQTT连接测试类
```

## ⚙️ 配置说明

项目通过 `application.yml` 进行配置：

```yaml
spring:
  main:
    web-application-type: none  # 屏蔽Spring Boot默认Web服务

# MQTT服务配置
mqtt:
  # 服务端口
  port: 8888
  # 主线程数
  boss-thread: 1
  # 工作线程数
  worker-thread: 1
  # 认证配置
  auth:
    # 登录权限开关
    login-enabled: false
    # 用户名
    username: admin
    # 密码
    password: 123456
    # 客户端ID验证开关
    client-id-validation: false
    # 允许的客户端ID列表
    allowed-client-ids:
      - c1
      - c2
      - c3
  # QoS配置
  qos:
    # 默认QoS级别
    default-level: 1
  # 消息格式验证
  message:
    # JSON格式验证开关
    json-validation: false

# WebSocket服务配置
websocket:
  # 服务端口
  port: 9010
  # 主线程数
  boss-thread: 1
  # 工作线程数
  worker-thread: 4
```

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd noodle-mqtt-netty
```

### 2. 配置修改

根据需要修改 `src/main/resources/application.yml` 中的配置。

### 3. 编译项目

```bash
mvn clean compile
```

### 4. 运行服务

```bash
mvn spring-boot:run
```

或者

```bash
mvn clean package
java -jar target/mqtt-netty-server-0.0.1-SNAPSHOT.jar
```

## 📊 配置参数详解

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `mqtt.port` | 8888 | MQTT服务监听端口 |
| `mqtt.boss-thread` | 1 | Netty主线程数 |
| `mqtt.worker-thread` | 1 | Netty工作线程数 |
| `mqtt.auth.login-enabled` | false | 是否启用登录认证 |
| `mqtt.auth.username` | admin | 认证用户名 |
| `mqtt.auth.password` | 123456 | 认证密码 |
| `mqtt.auth.client-id-validation` | false | 是否启用客户端ID验证 |
| `mqtt.auth.allowed-client-ids` | - | 允许连接的客户端ID列表 |
| `mqtt.qos.default-level` | 1 | 默认QoS级别（0/1/2） |
| `mqtt.message.json-validation` | false | 是否验证消息为JSON格式 |
| `websocket.port` | 9010 | WebSocket服务监听端口 |
| `websocket.boss-thread` | 1 | WebSocket主线程数 |
| `websocket.worker-thread` | 4 | WebSocket工作线程数 |

## 🧪 测试说明

项目包含一个完整的 MQTT 连接测试类 `ConnectionTest.java`：

```bash
# 确保MQTT服务已在端口8888启动
mvn exec:java -Dexec.mainClass="com.my.ConnectionTest"
```

## 📝 使用场景

- 🌐 物联网(IoT)设备通信平台
- ⚡ 实时消息推送系统
- 🏢 企业内部消息中间件
- 📚 学习MQTT协议和Netty网络编程

## 🔧 配置定制

项目支持通过 `application.yml` 文件进行灵活配置：

### 服务端口配置
```yaml
mqtt:
  port: 8888  # 修改MQTT服务监听端口
```

### 线程配置优化
```yaml
mqtt:
  boss-thread: 2    # 调整主线程数
  worker-thread: 4  # 调整工作线程数
```

### 安全认证配置
```yaml
mqtt:
  auth:
    login-enabled: true      # 启用登录认证
    username: your-username  # 设置用户名
    password: your-password  # 设置密码
```

### 客户端管理
```yaml
mqtt:
  auth:
    client-id-validation: true  # 启用客户端ID验证
    allowed-client-ids:         # 设置允许的客户端ID列表
      - client1
      - client2
```

## 📋 注意事项

- ⚠️ 项目基于 Java 8 开发，请确保运行环境兼容
- ⚠️ 如需启用 Web 功能，请修改 `application.yml` 配置文件
- ⚠️ 生产环境部署时，请根据实际需求调整安全配置
- ⚠️ 建议根据服务器性能调整线程配置参数
- ⚠️ 定期检查日志文件，监控服务运行状态

## 📄 许可证

本项目采用 Apache License 2.0 许可证，详情请参见 [LICENSE](LICENSE) 文件。