# 学生管理与用户认证后台系统

## 一、项目简介

本项目是一个基于 Spring Boot 的学生管理与用户认证后台系统，采用 Controller、Service、Mapper 分层架构，实现了学生信息管理、用户注册登录、JWT 登录认证、Redis 缓存、操作日志、Docker 容器化部署、Nginx 双实例负载均衡以及深分页优化等功能。

项目最初以学生信息 CRUD 为基础，后续逐步加入登录鉴权、缓存优化、多实例部署和异步日志等工程化能力，用于模拟真实 Java 后端项目中的常见开发场景。

---

## 二、技术栈

| 分类          | 技术                         |
| ----------- | -------------------------- |
| 后端框架        | Spring Boot                |
| 持久层         | MyBatis                    |
| 数据库         | MySQL                      |
| 缓存          | Redis                      |
| 登录认证        | Session、Spring Session、JWT |
| 密码加密        | BCrypt                     |
| 容器化         | Docker、Docker Compose      |
| 反向代理 / 负载均衡 | Nginx                      |
| 异步任务        | Spring `@Async`            |
| 构建工具        | Maven                      |
| 接口测试        | Postman                    |

---

## 三、项目功能

### 1. 学生管理模块

实现学生信息的基础管理功能：

* 新增学生
* 删除学生
* 修改学生
* 查询全部学生
* 根据 ID 查询学生
* 分页查询学生
* 按姓名查询学生
* 按年龄查询学生
* 动态 SQL 条件查询
* 游标分页 / 深分页优化查询

### 2. 用户模块

实现用户注册与登录功能：

* 用户注册
* 用户登录
* 用户密码 BCrypt 哈希存储
* 登录成功返回 JWT Token
* 获取当前登录用户
* 退出登录

### 3. 登录认证模块

项目实现了两类登录认证方案：

#### Session 登录

早期版本使用 HttpSession 保存用户登录状态。

#### Spring Session + Redis

将 Session 统一存储到 Redis 中，解决多实例部署时传统 Session 无法共享的问题。

#### JWT Token 登录

登录成功后服务端生成 JWT Token，客户端后续请求通过请求头携带：

```http
Authorization: Bearer <token>
```

后端拦截器解析并校验 Token，校验通过后放行请求。

### 4. Redis 缓存模块

项目引入 Redis 作为缓存层，主要缓存以下数据：

* 根据学生 ID 查询结果
* 学生列表查询结果
* 不存在学生的空值缓存

缓存策略：

* 查询学生时先查 Redis，缓存未命中再查 MySQL
* MySQL 查询成功后将结果写入 Redis
* 查询不存在的数据时写入短时间空值缓存，降低缓存穿透风险
* 新增、修改、删除学生后主动删除相关缓存，保证缓存一致性

### 5. 操作日志模块

项目新增 `operation_log` 表，记录关键操作日志，包括：

* 用户登录
* 新增学生
* 修改学生
* 删除学生

日志字段包括：

* 操作用户 ID
* 用户名
* 操作类型
* 操作描述
* 请求 IP
* 操作是否成功
* 创建时间

日志写入使用 Spring `@Async` 异步执行，避免日志写库阻塞主业务响应。

### 6. Docker + Nginx 多实例部署

项目支持使用 Docker Compose 一键启动：

* MySQL 容器
* Redis 容器
* Nginx 容器
* Spring Boot 实例 1
* Spring Boot 实例 2

请求流程：

```text
Client / Postman
        ↓
      Nginx
        ↓
Spring Boot Instance 1
Spring Boot Instance 2
        ↓
   MySQL + Redis
```

Nginx 将请求分发到两个 Spring Boot 实例，实现基础负载均衡。

---

## 四、系统架构

```text
用户 / Postman
    ↓
Nginx 反向代理
    ↓
Spring Boot 实例 1 / Spring Boot 实例 2
    ↓
Service 业务层
    ↓
MyBatis Mapper
    ↓
MySQL

Spring Boot
    ↓
Redis 缓存

Spring Boot
    ↓
@Async 异步操作日志
    ↓
operation_log 表
```

---

## 五、数据库设计

### 1. 学生表 `student`

```sql
CREATE TABLE IF NOT EXISTS student (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT
);
```

### 2. 用户表 `sys_user`

```sql
CREATE TABLE IF NOT EXISTS sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
```

### 3. 操作日志表 `operation_log`

```sql
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    username VARCHAR(50),
    operation_type VARCHAR(50),
    operation_desc VARCHAR(255),
    request_ip VARCHAR(50),
    success TINYINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 六、统一返回格式

项目封装统一返回对象 `Result`，接口统一返回格式如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "age": 20
    }
  ]
}
```

---

## 七、核心接口说明

### 1. 用户注册

```http
POST /users/register
```

请求体：

```json
{
  "username": "tom",
  "password": "123456"
}
```

---

### 2. 用户登录

```http
POST /users/login
```

请求体：

```json
{
  "username": "tom",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "tom",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

### 3. 获取当前登录用户

```http
GET /users/me
```

请求头：

```http
Authorization: Bearer <token>
```

---

### 4. 新增学生

```http
POST /students
```

请求头：

```http
Authorization: Bearer <token>
Content-Type: application/json
```

请求体：

```json
{
  "id": 1,
  "name": "张三",
  "age": 20
}
```

---

### 5. 查询全部学生

```http
GET /students
```

请求头：

```http
Authorization: Bearer <token>
```

---

### 6. 根据 ID 查询学生

```http
GET /students/{id}
```

示例：

```http
GET /students/1
```

---

### 7. 修改学生

```http
PUT /students
```

请求体：

```json
{
  "id": 1,
  "name": "张三",
  "age": 21
}
```

---

### 8. 删除学生

```http
DELETE /students/{id}
```

示例：

```http
DELETE /students/1
```

---

### 9. 普通分页查询

```http
GET /students/page?pageNum=1&pageSize=10
```

普通分页基于：

```sql
LIMIT offset, pageSize
```

适合数据量较小或需要跳转指定页码的场景。

---

### 10. 游标分页 / 深分页优化查询

```http
GET /students/scroll?lastId=0&pageSize=20
```

请求示例：

```http
GET /students/scroll?lastId=0&pageSize=2
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "age": 20
      },
      {
        "id": 2,
        "name": "李四",
        "age": 21
      }
    ],
    "nextLastId": 2,
    "hasNext": true
  }
}
```

---

## 八、Redis 缓存设计

### 1. 学生详情缓存

Key 示例：

```text
student:1
```

查询流程：

```text
先查 Redis
  ↓
命中：直接返回
  ↓
未命中：查询 MySQL
  ↓
写入 Redis
  ↓
返回结果
```

### 2. 学生列表缓存

Key 示例：

```text
student:list:all
```

### 3. 空值缓存

当查询不存在的学生 ID 时，向 Redis 写入短时间空值缓存：

```text
student:999 -> "null"
```

用于降低缓存穿透风险。

### 4. 缓存一致性处理

学生数据发生变化时主动删除相关缓存：

* 新增学生：删除学生列表缓存
* 修改学生：删除学生详情缓存和学生列表缓存
* 删除学生：删除学生详情缓存和学生列表缓存

---

## 九、JWT 登录认证流程

```text
用户登录
  ↓
校验用户名和密码
  ↓
生成 JWT Token
  ↓
返回给客户端
  ↓
客户端后续请求携带 Authorization 请求头
  ↓
拦截器解析 Token
  ↓
校验通过后放行
```

请求头格式：

```http
Authorization: Bearer <token>
```

JWT 中保存：

* 用户 ID
* 用户名
* 签发时间
* 过期时间

注意：JWT 只做签名校验，不应存放密码等敏感信息。

---

## 十、Spring Session + Redis

项目早期使用 HttpSession 保存登录状态，但普通 Session 默认存储在单个 Tomcat 实例内存中。

在多实例部署场景下，如果用户登录请求访问实例 1，而后续请求被 Nginx 转发到实例 2，实例 2 可能无法识别登录状态。

因此项目接入 Spring Session + Redis，将 Session 统一存储到 Redis 中，实现多实例 Session 共享。

---

## 十一、Docker Compose 部署

### 1. 打包项目

```bash
mvn clean package -DskipTests
```

### 2. 启动服务

```bash
docker compose up -d --build
```

### 3. 查看容器

```bash
docker ps
```

预期容器包括：

```text
studentms-mysql
studentms-redis
studentms-nginx
studentms-app-1
studentms-app-2
```

### 4. 停止服务

```bash
docker compose down
```

### 5. 停止服务并删除数据卷

```bash
docker compose down -v
```

注意：`docker compose down -v` 会删除 MySQL 数据卷，数据库数据会被清空。

---

## 十二、Nginx 负载均衡

Nginx 配置两个 Spring Boot 后端实例：

```nginx
upstream studentms_backend {
    server studentms-app-1:8080;
    server studentms-app-2:8080;
}
```

请求通过 Nginx 统一入口访问：

```text
http://localhost:8080
```

Nginx 将请求分发到两个 Spring Boot 实例。

可以通过测试接口 `/server/info` 多次请求，观察返回的容器名称是否变化，从而验证负载均衡是否生效。

---

## 十三、异步操作日志

项目使用 `@Async` 实现异步日志写入。

同步日志的问题：

```text
主业务操作
  ↓
写操作日志
  ↓
日志写完后接口才返回
```

异步日志流程：

```text
主业务操作成功
  ↓
提交日志写入任务
  ↓
接口直接返回
  ↓
日志由异步线程写入数据库
```

这样可以避免操作日志写入阻塞主业务响应。

当前记录的操作类型包括：

```text
USER_LOGIN
STUDENT_ADD
STUDENT_UPDATE
STUDENT_DELETE
```

---

## 十四、深分页优化

项目同时提供普通分页和游标分页两种方式。

### 1. 普通分页

普通分页使用：

```sql
SELECT * FROM student
ORDER BY id
LIMIT offset, pageSize;
```

当查询深页码时，例如第 950 页，每页 50 条：

```text
offset = (950 - 1) * 50 = 47450
```

数据库需要先扫描并跳过大量记录，再返回当前页数据，页码越深性能越差。

### 2. 游标分页

游标分页使用上一页最后一条记录的 ID 作为游标：

```sql
SELECT * FROM student
WHERE id > #{lastId}
ORDER BY id ASC
LIMIT #{pageSize};
```

接口示例：

```http
GET /students/scroll?lastId=0&pageSize=20
```

返回字段：

| 字段         | 说明              |
| ---------- | --------------- |
| records    | 当前页数据           |
| nextLastId | 下一次请求使用的 lastId |
| hasNext    | 是否还有下一页         |

优点：

* 可以利用主键索引
* 避免大 offset 扫描
* 适合滚动加载或“加载更多”场景

缺点：

* 不适合直接跳转到指定页码
* 需要有稳定排序规则

---

## 十五、项目截图

建议将测试截图保存到 `screenshots` 目录。

示例：

```text
screenshots/
├── docker-ps.png
├── login-token.png
├── query-students.png
├── redis-cache.png
├── nginx-load-balance.png
├── operation-log.png
└── cursor-page.png
```

### 1. Docker 容器启动

![Docker 容器启动](screenshots/docker-ps.png)

### 2. 登录接口返回 Token

![登录接口返回 Token](screenshots/login-token.png)

### 3. 查询学生列表

![查询学生列表](screenshots/query-students.png)

### 4. Redis 缓存 Key

![Redis 缓存](screenshots/redis-cache.png)

### 5. Nginx 负载均衡测试

![Nginx 负载均衡测试](screenshots/nginx-load-balance.png)

### 6. 异步操作日志

![异步操作日志](screenshots/operation-log.png)

### 7. 游标分页测试

![游标分页测试](screenshots/cursor-page.png)

---

## 十六、项目亮点

* 基于 Spring Boot + MyBatis + MySQL 实现学生信息增删改查、分页查询和动态 SQL 条件查询。
* 使用 BCrypt 对用户密码进行哈希存储，避免明文密码入库。
* 基于 JWT 实现 Token 登录认证，并通过拦截器统一校验登录状态。
* 引入 Redis 缓存学生详情和学生列表，减少重复数据库查询。
* 通过空值缓存降低缓存穿透风险，并在数据变更后主动删除相关缓存。
* 接入 Spring Session + Redis，实现多实例场景下的 Session 共享。
* 使用 Docker Compose 编排 MySQL、Redis、Nginx 和两个 Spring Boot 实例，实现一键部署。
* 通过 Nginx 实现反向代理和负载均衡，模拟多实例部署场景。
* 设计 operation_log 操作日志表，并使用 `@Async` 异步写入日志，降低主业务响应耗时。
* 新增游标分页接口，优化普通分页在深分页场景下的性能问题。

---

## 十七、后续优化方向

* 引入 Spring Security 统一认证与权限控制。
* 增加角色权限模型，实现管理员与普通用户区分。
* 使用 AOP + 自定义注解统一记录操作日志，减少 Controller 中的重复代码。
* 引入统一参数校验框架，如 Jakarta Validation。
* 增加接口限流，防止高频请求影响系统稳定性。
* 增加 Docker 健康检查和服务启动顺序控制。
* 针对热点缓存加入互斥锁或逻辑过期，进一步优化缓存击穿问题。

---

## 十八、项目总结

本项目从基础 CRUD 出发，逐步加入登录认证、Redis 缓存、统一异常处理、JWT、Spring Session、Docker 容器化、Nginx 负载均衡、异步操作日志和深分页优化等功能，覆盖了 Java 后端开发中常见的业务开发、缓存设计、登录鉴权、部署和性能优化场景。

通过该项目，能够较完整地展示 Spring Boot 后端项目从基础功能开发到工程化增强的过程。
