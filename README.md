# Library Management

基于 Spring Boot 3、MyBatis、MySQL 8、Vue 3 和 Element Plus 的前后端分离图书管理系统。

## 功能概览

- JWT 登录鉴权，管理员与读者两类角色权限控制。
- 图书、分类、读者、管理员和公告管理。
- 借书、正常归还、异常归还、库存联动及借阅时间线。
- 图书分类统计。
- DeepSeek AI 图书推荐和图书馆运营建议。

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.2.5、Spring MVC、MyBatis 3、JWT |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3、Vite 4、Element Plus、Axios |
| 网关 | Nginx |

## 项目结构

```text
LibraryManagement/
├─ src/
│  ├─ main/
│  │  ├─ java/com/library/
│  │  │  ├─ LibraryManagementApplication.java  # 启动类
│  │  │  ├─ common/                            # 统一响应、JWT、异常、通用工具
│  │  │  ├─ config/                            # Web、密码等配置
│  │  │  ├─ controller/                        # REST 接口
│  │  │  ├─ service/                           # 业务接口与实现
│  │  │  ├─ mapper/                            # MyBatis Mapper 接口
│  │  │  ├─ model/                             # entity、dto、vo
│  │  │  ├─ interceptor/                       # 登录与角色拦截器
│  │  │  └─ exception/                         # 全局异常处理
│  │  └─ resources/
│  │     ├─ application.yml                    # 应用配置
│  │     └─ mapper/                            # MyBatis XML
│  └─ test/
├─ frontend/                                   # Vue 前端
├─ sql/schema.sql                              # MySQL 建表与初始数据
├─ nginx/nginx.conf                            # Nginx 示例配置
└─ pom.xml
```

## 环境要求

- JDK 17
- Maven 3.8+
- MySQL 8.0
- Node.js 16+（推荐 18+）

## 快速启动

### 1. 初始化数据库

使用 Navicat、MySQL Workbench 或命令行执行 [sql/schema.sql](sql/schema.sql)。脚本会创建 `library_management` 数据库及相关数据表。

### 2. 配置后端环境变量

在项目根目录打开 PowerShell，按实际数据库账号调整后执行：

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "library_management"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "123456"
$env:JWT_SECRET = "replace-with-a-long-random-secret-in-production"
```

DeepSeek 功能可选。需要启用时再设置：

```powershell
$env:DEEPSEEK_API_KEY = "your-deepseek-api-key"
```

也可以在项目根目录创建 `config/application-local.yml`：

```yaml
deepseek:
  api-key: your-deepseek-api-key
```

该本地配置文件已被 Git 忽略。

不要将真实数据库密码、JWT 密钥或 DeepSeek Key 提交到 Git 仓库。

### 3. 启动后端

```powershell
mvn clean package
java -jar target/library-management-1.0.0.jar
```

后端默认地址为 `http://localhost:8081`。

也可以在 IDEA 中直接运行 `com.library.LibraryManagementApplication`。

### 4. 启动前端

```powershell
cd frontend
npm install
npm run dev
```

打开 Vite 输出的本地地址，默认通常为 `http://localhost:5173`。开发服务器会将 `/api` 请求代理到 `http://localhost:8081`。

## 初始账号

| 角色 | 账号 | 原始密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123456` |
| 读者 | `reader` | `123456` |

登录框中应填写原始密码 `123456`，不要填写数据库中的 BCrypt 密文。管理员首次成功登录后，旧的明文初始密码会自动升级为 BCrypt 密文。

## API 概览

所有接口统一以 `/api` 为前缀，响应格式如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 模块 | 接口前缀 |
| --- | --- |
| 登录、登出、当前用户 | `/api/auth` |
| 图书与统计 | `/api/books` |
| 分类 | `/api/categories` |
| 读者 | `/api/readers` |
| 管理员 | `/api/admins` |
| 公告 | `/api/notices` |
| 借阅 | `/api/borrows` |
| AI 服务 | `/api/ai` |

除登录和公开公告接口外，请在请求头添加：

```text
Authorization: Bearer <token>
```

## 生产部署

构建前端：

```powershell
cd frontend
npm run build
```

将 `frontend/dist` 部署到 Nginx 静态目录，并参考 [nginx/nginx.conf](nginx/nginx.conf) 将 `/api/` 反向代理到后端 `8081` 端口。

## 常见问题

**后端启动提示端口被占用**

默认端口是 `8081`。可使用以下命令检查：

```powershell
netstat -ano | findstr ":8081"
```

**登录返回服务器内部错误**

确认 MySQL 已启动、`library_management` 数据库已初始化，并检查 `DB_USERNAME` 与 `DB_PASSWORD` 是否正确。

**Git 无法推送 GitHub**

确认 Git 命令行能访问 `github.com:443`，并检查是否配置了失效的 Git 代理：

```powershell
git config --global --get http.proxy
git config --global --get https.proxy
```
