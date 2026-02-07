# 健身房会员管理系统

基于 Spring Boot 3 + Vue 3 + Element Plus 构建的健身房会员管理系统。

## 题目内容

1． 选题要求：用IDEA做一个健身房会员管理系统。
2． 设计要求：每个同学至少完成两个功能模块，每个功能模块必须包含完整的数据增、删、改、查功能。

## 快速开始

### 环境要求

- Docker Desktop
  - macOS: 支持 Intel (x86_64) 和 Apple Silicon (M1/M2/M3/M4)
  - Windows: Windows 10/11 64-bit
  - Linux: 支持主流发行版

### 启动项目

**macOS / Linux:**

```bash
# 克隆项目
git clone <repository-url>
cd gym-management

# 启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

**Windows (PowerShell):**

```powershell
# 克隆项目
git clone <repository-url>
cd gym-management

# 启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost:8081 | Vue 3 前端管理界面 |
| 后端 API | http://localhost:8080 | RESTful API 服务 |
| MySQL | localhost:3307 | 数据库 |

### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |

## 功能特性

- **会员管理**：会员信息增删改查、状态管理
- **卡种管理**：月卡/季卡/年卡/次卡类型管理
- **开卡管理**：会员开卡记录、到期管理
- **操作日志**：系统操作记录查询
- **数据统计**：首页仪表盘数据概览
- **登录认证**：JWT Token 认证

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot 3 + MyBatis-Plus + MySQL 8.0 |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Axios |
| 部署 | Docker + Docker Compose + Nginx |
| 认证 | JWT |

## 项目结构

```
├── backend/                    # 后端服务 (Spring Boot)
│   ├── src/main/java/com/gym/
│   │   ├── controller/         # 控制器
│   │   ├── service/            # 业务逻辑
│   │   ├── mapper/             # 数据访问
│   │   ├── entity/             # 实体类
│   │   ├── config/             # 配置类
│   │   ├── aop/                # AOP 切面（操作日志）
│   │   ├── interceptor/        # 拦截器（JWT认证）
│   │   └── util/               # 工具类
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── schema.sql          # 数据库脚本
├── frontend-admin/             # 前端管理后台 (Vue 3)
│   └── src/
│       ├── api/                # API 接口
│       ├── views/              # 页面组件
│       ├── router/             # 路由配置
│       ├── store/              # 状态管理
│       └── styles/             # 样式文件
├── docs/                       # 文档
│   └── project_design.md       # 设计文档
└── docker-compose.yml          # Docker 编排
```

## 常用命令

```bash
# 启动服务
docker-compose up -d --build

# 停止服务
docker-compose down

# 清理数据重新开始
docker-compose down -v
docker-compose up -d --build

# 查看指定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

## 注意事项

- 首次启动需要等待 MySQL 初始化完成（约 30 秒）
- 如遇端口冲突，可修改 `docker-compose.yml` 中的端口映射
- 数据库数据持久化在 Docker volume `mysql_data` 中
