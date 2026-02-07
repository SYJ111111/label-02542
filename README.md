# 健身房会员管理系统

## 快速启动

```bash
# 确保已安装 Docker 和 Docker Compose
docker-compose up --build

# 访问地址：http://localhost:8081
# 默认账号：admin / 123456
```

## 服务信息

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端管理后台 | http://localhost:8081 | Vue 3 + Element Plus |
| 后端 API | http://localhost:8080/api | Spring Boot 3 |
| MySQL 数据库 | localhost:3307 | MySQL 8.0 |

## 功能模块

- **会员管理**：会员信息增删改查、状态管理
- **卡种管理**：月卡/季卡/年卡/次卡类型管理
- **开卡管理**：会员开卡记录、到期管理
- **操作日志**：登录/退出/业务操作记录
- **登录认证**：JWT 认证、登录退出日志记录

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Axios |
| 后端 | Java 17 + Spring Boot 3 + MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 部署 | Docker + Docker Compose + Nginx |

## 项目结构

```
├── backend/                   # Spring Boot 后端
│   ├── src/main/java/com/gym/
│   │   ├── controller/        # 控制器（Auth/Member/CardType/MemberCard/Log）
│   │   ├── service/           # 业务逻辑
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── entity/            # 实体类
│   │   ├── aop/               # 操作日志切面
│   │   └── config/            # 配置类
│   └── src/main/resources/
│       ├── application.yml
│       └── schema.sql
├── frontend-admin/            # Vue 3 前端
│   └── src/
│       ├── views/             # 页面（login/dashboard/member/cardType/memberCard/log）
│       ├── api/               # API 接口
│       ├── store/             # Pinia 状态管理
│       └── router/            # 路由配置
├── docker-compose.yml
└── docs/project_design.md
```
