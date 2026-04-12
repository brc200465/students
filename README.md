# 学生管理与用户登录后台系统

## 1. 项目简介
本项目是一个基于 **Spring Boot + MyBatis + MySQL + Redis** 开发的后端练手项目，主要实现了学生信息管理与用户注册登录等基础后台功能。

项目在最小 CRUD 的基础上，逐步补充了以下内容：

- Controller / Service / Mapper 分层设计
- 统一返回结果封装
- 全局异常处理
- 用户注册与登录
- BCrypt 密码加密
- Session 登录状态保持
- 登录拦截器
- Redis 单条查询缓存、列表缓存、空值缓存

该项目的目标是模拟一个更接近真实后台开发场景的 Java 后端项目，为后续实习投递和面试做准备。

---

## 2. 技术栈
- Java
- Spring Boot
- MyBatis
- MySQL
- Redis
- Maven

---

## 3. 项目结构
```text
src/main/java/com/example/studentms
├── controller          // 控制层，接收请求并返回结果
├── service             // 业务层接口
├── service/impl        // 业务层实现类
├── mapper              // 数据访问层
├── entity              // 实体类
├── result              // 统一返回结果类
├── exception           // 自定义异常和全局异常处理
├── interceptor         // 登录拦截器
└── config              // Spring MVC 配置类
```

---

## 4. 已实现功能

### 4.1 学生模块
- 查询全部学生
- 根据 id 查询学生
- 新增学生
- 修改学生
- 删除学生
- 分页查询
- 按姓名查询
- 按年龄查询

### 4.2 用户模块
- 根据用户名查询用户
- 用户注册
- 用户登录
- 获取当前登录用户
- 退出登录

### 4.3 项目通用功能
- Controller / Service / Mapper 分层设计
- 统一返回结果 `Result`
- 自定义业务异常 `BusinessException`
- 全局异常处理
- Session 登录状态保持
- 登录拦截器
- Redis 缓存
- 基础参数校验

---

## 5. 统一返回格式
项目中的接口统一返回如下格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

字段说明：

- `code`：状态码
- `message`：提示信息
- `data`：实际返回的数据

成功返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Tom",
    "age": 20
  }
}
```

失败返回示例：

```json
{
  "code": 404,
  "message": "student not found",
  "data": null
}
```

---

## 6. 数据库设计

### 6.1 学生表 `student`
```sql
CREATE TABLE student (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT
);
```

### 6.2 用户表 `sys_user`
```sql
CREATE TABLE sys_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
```

---

## 7. 核心功能说明

### 7.1 分层设计
项目采用典型的三层结构：

- `Controller`：接收请求、返回结果
- `Service`：处理业务逻辑
- `Mapper`：与数据库交互

这样做的好处是：

- 代码结构更清晰
- 职责分离更明确
- 更符合企业项目开发习惯

---

### 7.2 用户注册与登录

#### 注册
- 接收用户名和密码
- 参数校验
- 判断用户名是否已存在
- 使用 `BCryptPasswordEncoder` 对密码加密
- 将加密后的密码存入数据库

#### 登录
- 根据用户名查询用户
- 使用 `matches()` 校验明文密码与数据库加密密码是否匹配
- 登录成功后，将用户信息保存到 `HttpSession`
- 后续请求通过 Session 判断当前用户是否已登录

---

### 7.3 Session 登录状态保持
登录成功后，会将当前用户信息写入 Session，例如：

```java
session.setAttribute("loginUserId", loginUser.getId());
session.setAttribute("loginUsername", loginUser.getUsername());
```

退出登录时使用：

```java
session.invalidate();
```

让当前 Session 失效，从而清除登录状态。

---

### 7.4 登录拦截器
项目通过 `HandlerInterceptor` 编写了登录拦截器，对需要登录才能访问的接口进行统一拦截。

实现逻辑：

- 请求先进入拦截器
- 通过 `request.getSession(false)` 获取当前 Session
- 判断 Session 是否存在，及其中是否存在 `loginUserId`
- 如果未登录，则返回未登录信息
- 如果已登录，则放行

放行路径包括：

- `/users/login`
- `/users/register`
- `/error`

---

### 7.5 全局异常处理
项目使用：

- `@RestControllerAdvice`
- `@ExceptionHandler`

实现全局异常处理。

同时定义了自定义业务异常 `BusinessException`，用于统一处理：

- 参数错误
- 查询不到数据
- 用户名已存在
- 登录失败
- 删除失败等业务异常

普通异常则由：

```java
@ExceptionHandler(Exception.class)
```

统一兜底处理，避免异常信息直接暴露给前端。

---

## 8. Redis 缓存设计

项目中引入 Redis 作为缓存层，主要用于提升学生查询接口的访问性能，减少对 MySQL 的直接访问压力。

### 8.1 已实现的缓存场景

#### （1）根据 id 查询学生缓存
接口：

```http
GET /students/{id}
```

缓存 key 示例：

```text
student:1
student:2
```

实现逻辑：

1. 先查 Redis
2. Redis 有数据，直接返回
3. Redis 没有数据，再查 MySQL
4. MySQL 查到数据后，序列化为 JSON 写入 Redis
5. 设置过期时间
6. 返回查询结果

对于数据库中不存在的数据，项目使用空值缓存：

```text
null
```

并设置较短过期时间，用于初步解决缓存穿透问题。

---

#### （2）查询全部学生列表缓存
接口：

```http
GET /students
```

缓存 key：

```text
student:list:all
```

实现逻辑：

1. 先查 Redis
2. Redis 有数据，直接返回
3. Redis 没有数据，再查 MySQL
4. 将查询结果序列化为 JSON 写入 Redis
5. 设置过期时间
6. 返回查询结果

---

### 8.2 缓存一致性处理
为了避免脏数据，项目对新增、修改、删除操作进行了缓存清理：

- 新增学生后：删除学生列表缓存
- 修改学生后：删除单个学生缓存和学生列表缓存
- 删除学生后：删除单个学生缓存和学生列表缓存

---

### 8.3 缓存问题处理

#### 缓存穿透
**定义：**  
查询数据库中不存在的数据，Redis 中也没有，导致请求绕过缓存一直打到数据库。

**当前解决方法：**
- 空值缓存

---

#### 缓存击穿
**定义：**  
某个热点 key 过期时，大量请求同时访问该 key，结果一起打到数据库。

**常见解决方法：**
- 热点 key 不轻易过期
- 逻辑过期
- 加互斥锁

---

#### 缓存雪崩
**定义：**  
大量缓存 key 在同一时间集中失效，导致大量请求一起访问数据库。

**常见解决方法：**
- 过期时间加随机值
- 服务降级
- 限流

---

## 9. 接口清单

---

### 9.1 学生模块接口

#### 1. 查询全部学生
- 请求方式：GET
- 请求路径：`/students`
- 请求参数：无

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Tom",
      "age": 20
    }
  ]
}
```

---

#### 2. 根据 id 查询学生
- 请求方式：GET
- 请求路径：`/students/{id}`
- 请求参数：
  - `id`：学生 id

成功返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Tom",
    "age": 20
  }
}
```

查询不到时返回示例：
```json
{
  "code": 404,
  "message": "student not found",
  "data": null
}
```

---

#### 3. 新增学生
- 请求方式：POST
- 请求路径：`/students`
- 请求体：

```json
{
  "id": 1,
  "name": "Tom",
  "age": 20
}
```

成功返回示例：
```json
{
  "code": 200,
  "message": "新增成功",
  "data": null
}
```

参数错误返回示例：
```json
{
  "code": 400,
  "message": "name 不能为空",
  "data": null
}
```

---

#### 4. 修改学生
- 请求方式：PUT
- 请求路径：`/students`
- 请求体：

```json
{
  "id": 1,
  "name": "Tom",
  "age": 21
}
```

成功返回示例：
```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

---

#### 5. 删除学生
- 请求方式：DELETE
- 请求路径：`/students/{id}`
- 请求参数：
  - `id`：学生 id

成功返回示例：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

#### 6. 分页查询
- 请求方式：GET
- 请求路径：`/students/page`
- 请求参数：
  - `pageNum`：页码，必须大于等于 1
  - `pageSize`：每页条数，必须大于等于 1

请求示例：
```text
/students/page?pageNum=1&pageSize=5
```

成功返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Tom",
      "age": 20
    }
  ]
}
```

参数错误返回示例：
```json
{
  "code": 400,
  "message": "pageNum 不能小于 1",
  "data": null
}
```

---

#### 7. 按姓名查询
- 请求方式：GET
- 请求路径：`/students/searchByName`
- 请求参数：
  - `name`：学生姓名

请求示例：
```text
/students/searchByName?name=Tom
```

成功返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Tom",
      "age": 20
    }
  ]
}
```

---

#### 8. 按年龄查询
- 请求方式：GET
- 请求路径：`/students/searchByAge`
- 请求参数：
  - `age`：学生年龄

请求示例：
```text
/students/searchByAge?age=20
```

成功返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Tom",
      "age": 20
    }
  ]
}
```

---

### 9.2 用户模块接口

#### 1. 根据用户名查询用户
- 请求方式：GET
- 请求路径：`/users/byUsername`
- 请求参数：
  - `username`：用户名

请求示例：
```text
/users/byUsername?username=tom
```

成功返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "tom",
    "password": null
  }
}
```

---

#### 2. 用户注册
- 请求方式：POST
- 请求路径：`/users/register`
- 请求体：

```json
{
  "username": "tom",
  "password": "123456"
}
```

成功返回示例：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

用户名重复返回示例：
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

---

#### 3. 用户登录
- 请求方式：POST
- 请求路径：`/users/login`
- 请求体：

```json
{
  "username": "tom",
  "password": "123456"
}
```

成功返回示例：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "tom",
    "password": null
  }
}
```

---

#### 4. 获取当前登录用户
- 请求方式：GET
- 请求路径：`/users/me`

未登录返回示例：
```json
{
  "code": 401,
  "message": "当前未登录",
  "data": null
}
```

---

#### 5. 退出登录
- 请求方式：POST
- 请求路径：`/users/logout`

成功返回示例：
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

## 10. 参数校验
当前项目已实现的基础参数校验包括：

- `pageNum` 不能小于 1
- `pageSize` 不能小于 1
- `name` 不能为空，也不能只包含空格
- `age` 不能小于 0
- `username` 不能为空
- `password` 不能为空
- 修改学生时 `id` 不能为空

---

## 11. 启动项目

### 11.1 启动 MySQL
确保本地 MySQL 服务已启动，并已创建数据库和表。

### 11.2 启动 Redis
在 Ubuntu/WSL 中执行：

```bash
sudo service redis-server start
```

测试 Redis 是否正常运行：

```bash
redis-cli ping
```

若返回：

```bash
PONG
```

说明 Redis 已启动成功。

---

### 11.3 配置数据库与 Redis
在 `src/main/resources/application.properties` 中配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/你的数据库名?serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=你的密码
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

mybatis.configuration.map-underscore-to-camel-case=true

spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
```

如 Redis 设置了密码，还需补充：

```properties
spring.data.redis.password=你的密码
```

---

### 11.4 启动 Spring Boot 项目
运行启动类，启动成功后即可通过浏览器、Postman 或 Apifox 测试接口。

---

## 12. 常用 Redis 命令

进入 Redis 客户端：

```bash
redis-cli
```

选择数据库：

```bash
SELECT 0
```

设置键值：

```bash
set name tom
```

获取键值：

```bash
get name
```

删除键：

```bash
del name
```

设置过期时间：

```bash
expire name 60
```

查看剩余过期时间：

```bash
ttl name
```

判断键是否存在：

```bash
exists name
```

查看当前数据库中的键：

```bash
keys *
keys student:*
```

---

## 13. 项目收获
通过该项目，掌握了以下内容：

- Spring Boot 基础 Web 开发
- MyBatis 操作数据库
- MySQL 基础增删改查
- Controller / Service / Mapper 分层设计
- 统一返回结果封装
- 分页查询与条件查询实现
- 用户注册与登录流程
- BCrypt 密码加密
- Session 登录状态保持
- 登录拦截器实现
- 全局异常处理
- Redis 单条缓存、列表缓存、空值缓存
- 缓存一致性与缓存穿透的基础处理思路

---

## 14. 后续可优化方向
- 引入 Swagger / OpenAPI 生成接口文档
- 使用参数校验注解优化参数校验逻辑
- 将密码、数据库、Redis 配置进一步拆分管理
- 引入 JWT 进行无状态登录
- 优化 Redis 缓存策略
- 增加日志记录
- 增加前后端联调
- 补充更复杂的业务模块

---

## 15. 项目亮点总结
- 基于 Spring Boot + MyBatis + MySQL 完成学生管理和用户登录后台功能开发
- 使用 BCryptPasswordEncoder 对密码进行加密存储，避免明文密码入库
- 使用 HttpSession 保持登录状态，并通过 HandlerInterceptor 编写登录拦截器
- 封装统一返回结果，结合自定义异常和全局异常处理提升接口规范性
- 引入 Redis 对学生单条查询和列表查询进行缓存，并通过空值缓存初步解决缓存穿透问题
- 对新增、修改、删除操作进行缓存清理，保证缓存与数据库数据的一致性