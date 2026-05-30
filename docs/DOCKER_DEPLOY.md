# 多媒体作品评审系统 — Docker 离线部署指南

## 一、系统架构

```
┌──────────────────────────────────────────────────────┐
│                    客户端浏览器                        │
│                  http://<主机IP>:80                    │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│              Frontend 容器 (Nginx :80)                 │
│                                                       │
│  /          → /usr/share/nginx/html (React SPA)       │
│  /api/*     → proxy_pass → http://backend:8080        │
└──────────────────────┬───────────────────────────────┘
                       │  Docker 内部网络
                       ▼
┌──────────────────────────────────────────────────────┐
│           Backend 容器 (Spring Boot :8080)             │
│                                                       │
│  H2 数据库  ← /app/data/ (volume)                     │
│  稿件文件    ← /app/uploads/ (volume)                 │
└──────────────────────────────────────────────────────┘
```

- Nginx 统一入口，前端静态文件 + API 反向代理一体化
- 后端不对外暴露端口，仅容器内网可访问
- 数据和文件通过 Docker volumes 持久化

---

## 二、环境要求

| 软件 | 最低版本 | 说明 |
|------|----------|------|
| Docker | 20.10+ | 需支持 `docker compose` (v2) |
| Docker Compose | 2.0+ | 内置于 Docker Desktop / 插件形式 |
| 磁盘空间 | ≥ 2 GB | 镜像约 500MB，数据空间另计 |
| 内存 | ≥ 512 MB | 建议 1 GB 以上 |
| 操作系统 | Linux (推荐) / Windows / macOS | 支持 Docker 即可 |

---

## 三、文件说明

离线部署包 `offline-package/` 包含以下文件：

| 文件 | 说明 |
|------|------|
| `multimedia-review-backend.tar` | 后端 Docker 镜像 (Spring Boot + JRE) |
| `multimedia-review-frontend.tar` | 前端 Docker 镜像 (Nginx + React) |
| `docker-compose.yml` | 容器编排配置 |
| `deploy.sh` | 一键部署脚本 |

---

## 四、部署前准备

### 4.1 确认 Docker 已安装并运行

```bash
docker --version
# 应输出: Docker version 20.10.x 或更高

docker compose version
# 应输出: Docker Compose version v2.x.x
```

### 4.2 检查端口占用

默认使用 **80 端口**。如 80 已被占用，请参考 7.2 节修改端口。

```bash
# Linux 检查端口占用
netstat -tlnp | grep :80
# 或
lsof -i :80
```

### 4.3 关闭防火墙或开放端口

```bash
# firewalld (CentOS/RHEL)
sudo firewall-cmd --add-port=80/tcp --permanent
sudo firewall-cmd --reload

# ufw (Ubuntu/Debian)
sudo ufw allow 80/tcp

# iptables
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
```

---

## 五、离线部署步骤

### 5.1 传输部署包

将 `offline-package/` 整个目录拷贝到目标机器的任意路径，例如：

```bash
scp -r offline-package/ user@目标服务器IP:/opt/multimedia-review/
```

### 5.2 进入部署目录

```bash
cd /opt/multimedia-review/offline-package/
```

### 5.3 执行部署

**方式一：一键脚本**

```bash
chmod +x deploy.sh
bash deploy.sh
```

**方式二：手动执行**

```bash
# 1. 加载镜像
docker load -i multimedia-review-backend.tar
docker load -i multimedia-review-frontend.tar

# 2. 验证镜像已加载
docker images | grep multimedia-review

# 3. 启动服务
docker compose up -d

# 4. 查看状态
docker compose ps
```

### 5.4 验证部署

```bash
# 查看容器状态（Status 应为 Up）
docker compose ps

# 查看后端日志
docker compose logs backend

# 查看前端日志
docker compose logs frontend

# 访问系统
curl http://localhost/api/competitions
```

浏览器打开 `http://<服务器IP>`，看到登录页面即部署成功。

---

## 六、配置说明

所有配置通过环境变量控制，在 `docker-compose.yml` 的 `environment` 字段中设置。

### 6.1 必改项

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `JWT_SECRET` | `change-me-in-production-use-a-strong-secret` | JWT 签名密钥，**部署后务必修改** |

### 6.2 可选项

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `FRONTEND_PORT` | `80` | 前端访问端口 |
| `SPRING_PROFILES_ACTIVE` | `dev` | Spring 环境 (`dev`=H2, `prod`=MySQL) |
| `FILE_UPLOAD_DIR` | `/app/uploads` | 上传文件存储路径 (容器内) |

### 6.3 使用 MySQL（可选）

当正式环境需要 MySQL 时，修改 `docker-compose.yml` 或创建覆盖文件 `docker-compose.prod.yml`：

```yaml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=your_db_user
      - DB_PASSWORD=your_db_password
      - JWT_SECRET=your-256-bit-secret-key-here
    # H2 数据卷可以移除
    # volumes 中移除 multimedia-review-data

  # 添加 MySQL 服务
  mysql:
    image: mysql:8.0
    container_name: multimedia-review-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: your_db_password
      MYSQL_DATABASE: multimedia_review
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - multimedia-review-net

volumes:
  mysql-data:
```

> MySQL 镜像不包含在离线包中，需在联网环境预先 `docker pull mysql:8.0` 并 `docker save`。

---

## 七、常用操作

### 7.1 查看日志

```bash
# 查看所有容器日志（实时）
docker compose logs -f

# 仅看后端
docker compose logs -f backend

# 查看最近 100 行
docker compose logs --tail=100 backend
```

### 7.2 修改端口

如果 80 端口被占用，启动时指定其他端口：

```bash
FRONTEND_PORT=8080 docker compose up -d
```

访问地址变为 `http://<服务器IP>:8080`。

### 7.3 重启服务

```bash
docker compose restart
```

### 7.4 停止服务

```bash
# 停止但不删除容器和数据
docker compose stop

# 停止并删除容器，保留数据卷
docker compose down
```

### 7.5 完全卸载

```bash
# 停止容器 + 删除数据卷（数据库和上传文件将丢失！）
docker compose down -v

# 删除镜像
docker rmi multimedia-review-backend:1.0.0 multimedia-review-frontend:1.0.0
```

### 7.6 更新部署

新版离线包更新步骤：

```bash
cd /opt/multimedia-review/offline-package/

# 1. 停止旧容器
docker compose down

# 2. 加载新镜像（覆盖旧标签）
docker load -i multimedia-review-backend.tar
docker load -i multimedia-review-frontend.tar

# 3. 启动新版本（数据卷不变，数据保留）
docker compose up -d
```

---

## 八、数据备份与恢复

### 8.1 备份数据卷

```bash
# H2 数据库文件
docker run --rm -v multimedia-review-multimedia-review-data:/data -v $(pwd):/backup alpine \
  tar czf /backup/h2-data-backup-$(date +%Y%m%d).tar.gz -C /data .

# 上传文件
docker run --rm -v multimedia-review-multimedia-review-uploads:/data -v $(pwd):/backup alpine \
  tar czf /backup/uploads-backup-$(date +%Y%m%d).tar.gz -C /data .
```

### 8.2 恢复数据卷

```bash
docker run --rm -v multimedia-review-multimedia-review-data:/data -v $(pwd):/backup alpine \
  tar xzf /backup/h2-data-backup-20260517.tar.gz -C /data
```

### 8.3 定时备份 (crontab)

```bash
# 每天凌晨 2 点备份
0 2 * * * cd /opt/multimedia-review/offline-package && \
  docker run --rm -v multimedia-review-multimedia-review-data:/data -v /backup:/backup alpine \
  tar czf /backup/multimedia-review-$(date +\%Y\%m\%d).tar.gz -C /data .
```

---

## 九、构建自定义镜像

如需重新构建镜像（修改代码后），在有 Docker 和 Maven 的联网机器上：

```bash
# Linux/Mac
cd /path/to/project
bash docker/build.sh

# Windows
cd /path/to/project
docker\build.bat
```

构建脚本会完成：
1. Maven 编译打包后端 (JAR)
2. npm 构建前端 (静态文件)
3. Docker 构建两个镜像
4. 导出镜像 tar 文件
5. 整理离线部署包到 `docker/offline-package/`

---

## 十、故障排查

### 10.1 容器启动后立即退出

```bash
docker compose logs backend
# 常见原因: 端口冲突、数据库文件损坏
```

### 10.2 前端页面空白

1. 确认前端容器状态：`docker compose ps frontend`
2. 确认后端可访问：`docker compose exec frontend wget -qO- http://backend:8080/api/competitions`
3. 浏览器 F12 查看网络请求是否 502/504

### 10.3 上传文件失败

- 检查后端磁盘空间：`df -h`
- 检查文件大小是否超过 500MB 限制

### 10.4 端口被占用

```bash
# 查看占用端口的进程
sudo lsof -i :80
# 使用其他端口启动
FRONTEND_PORT=8080 docker compose up -d
```

### 10.5 Docker 未安装 / 版本过低

```bash
# Ubuntu/Debian 安装最新 Docker
curl -fsSL https://get.docker.com | bash

# CentOS/RHEL 8+
sudo dnf install docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl enable docker --now
```

### 10.6 数据卷权限问题 (SELinux)

```bash
# CentOS/RHEL 如果遇到权限错误，在 docker-compose.yml 卷后加 :Z 标识
volumes:
  - multimedia-review-data:/app/data:Z
  - multimedia-review-uploads:/app/uploads:Z
```
