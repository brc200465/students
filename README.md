# 学生管理系统 v2

## 1. 项目简介
学生管理系统 v2 是一个基于 Spring Boot + MyBatis + MySQL 开发的后端练手项目。  
该项目在最小 CRUD 项目的基础上，进一步补充了分层设计、统一返回结果、分页查询、条件查询和基础参数校验，使项目更接近真实后台管理系统的开发方式。

---

## 2. 技术栈
- Java
- Spring Boot
- MyBatis
- MySQL
- Maven

---

## 3. 项目结构
```text
src/main/java/com/example/studentms
├── controller      // 控制层，接收请求并返回结果
├── service         // 业务层接口
├── service/impl    // 业务层实现类
├── mapper          // 数据访问层
├── entity          // 实体类
└── result          // 统一返回结果类
```

---

## 4. 已实现功能
- 查询全部学生
- 根据 id 查询学生
- 新增学生
- 修改学生
- 删除学生
- 分页查询
- 按姓名查询
- 按年龄查询
- 统一返回结果
- 基础参数校验

---

## 5. 统一返回格式
项目中的接口统一返回如下格式：

```json
{
  "code": 200,
  "message": "success",
  "data": ...
}
```

成功时返回示例：

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

失败时返回示例：

```json
{
  "code": 404,
  "message": "student not found",
  "data": null
}
```

---

## 6. 接口清单

### 6.1 查询全部学生
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

### 6.2 根据 id 查询学生
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

### 6.3 新增学生
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

### 6.4 修改学生
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

### 6.5 删除学生
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

### 6.6 分页查询
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

### 6.7 按姓名查询
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

### 6.8 按年龄查询
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

## 7. 参数校验
当前项目已实现的基础参数校验包括：

- `pageNum` 不能小于 1
- `pageSize` 不能小于 1
- `name` 不能为空，也不能只包含空格
- `age` 不能小于 0
- 修改学生时 `id` 不能为空

---

## 8. 数据库表示例
```sql
CREATE TABLE student (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT
);
```

---

## 9. 启动项目
1. 创建数据库
2. 执行建表 SQL
3. 在 `application.yml` 或 `application.properties` 中配置数据库连接
4. 启动 Spring Boot 项目
5. 使用浏览器、Postman 或 Apifox 测试接口

---

## 10. 项目收获
通过该项目，掌握了以下内容：

- Spring Boot 基础 Web 开发
- MyBatis 操作数据库
- MySQL 基础增删改查
- Controller / Service / Mapper 分层设计
- 统一返回结果封装
- 分页查询与条件查询实现
- 基础参数校验思路

---

## 11. 后续可优化方向
- 补充全局异常处理
- 使用参数校验注解优化校验逻辑
- 增加用户管理和登录功能
- 引入 Swagger / OpenAPI 生成接口文档
- 增加前后端联调