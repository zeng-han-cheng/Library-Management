# Library Management

前后端分离图书管理系统，包含 `common`、`pojo`、`server` 三个 Maven 模块和 `frontend` Vue 3 前端。

## 后端启动

1. 执行 `sql/schema.sql` 初始化 MySQL 8 数据库。
2. 设置 `DB_USERNAME`、`DB_PASSWORD`、`JWT_SECRET` 和可选的 `DEEPSEEK_API_KEY` 环境变量。
3. 在项目根目录执行 `mvn clean package`，再运行 `server/target/server-1.0.0.jar`。

默认管理员账号为 `admin`；密码请在数据库初始化后按部署要求设置，生产环境不要使用弱密码。

## 前端启动

在 `frontend` 目录执行 `npm install` 和 `npm run dev`。生产构建使用 `npm run build`，Nginx 示例见 `nginx/nginx.conf`。
