# 健身房会员管理系统

## How to Run

```bash
# 1. 确保已安装 Docker 和 Docker Compose
# 2. 在项目根目录执行
docker-compose up --build

# 3. 访问前端管理后台
# http://localhost:8081

# 4. 等待后端服务启动完成（首次启动需等待 MySQL 初始化）
```

## Services

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端管理后台 | http://localhost:8081 | Vue 3 + Element Plus |
| 后端 API | http://localhost:8080/api | Spring Boot 3 |
| MySQL 数据库 | localhost:3307 | MySQL 8.0 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |

## 题目内容

1． 选题要求：用IDEA做一个健身房会员管理系统。 
2． 设计要求：每个同学至少完成两个功能模块，每个功能模块必须包含完整的数据增、删、改、查功能。

---

## 项目介绍

本系统是一个健身房会员管理系统，用于管理健身房的日常会员业务。系统采用前后端分离架构，前端使用 Vue 3 + Element Plus，后端使用 Spring Boot 3 + MyBatis-Plus + MySQL 8.0。

### 核心功能模块

1. **会员管理**：会员信息的增删改查、状态管理
2. **卡种管理**：会员卡类型（月卡/季卡/年卡/次卡）的增删改查
3. **开卡管理**：会员开卡记录的增删改查、到期管理
4. **操作日志**：系统操作记录查询
5. **登录认证**：基于 JWT 的登录认证

### 技术栈

- **前端**：Vue 3 + Vite + Element Plus + Pinia + Axios + SCSS
- **后端**：Java 17 + Spring Boot 3 + MyBatis-Plus + MySQL 8.0
- **部署**：Docker + Docker Compose + Nginx

### 项目结构

```
label-02542/
├── README.md                  # 项目说明文档
├── docker-compose.yml         # Docker Compose 配置
├── .gitignore                 # Git 忽略文件
├── docs/
│   └── project_design.md      # 设计文档
├── backend/                   # 后端项目 (Spring Boot)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/gym/
│           │   ├── GymApplication.java
│           │   ├── config/
│           │   ├── common/
│           │   ├── controller/
│           │   ├── service/
│           │   ├── mapper/
│           │   ├── entity/
│           │   ├── dto/
│           │   ├── util/
│           │   ├── interceptor/
│           │   ├── aop/
│           │   └── annotation/
│           └── resources/
│               ├── application.yml
│               └── schema.sql
└── frontend-admin/            # 前端管理后台 (Vue 3)
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.js
        ├── App.vue
        ├── api/
        ├── store/
        ├── router/
        ├── views/
        ├── styles/
        └── utils/
```
