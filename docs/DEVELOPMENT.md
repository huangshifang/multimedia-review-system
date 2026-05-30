# 多媒体作品评审系统 — 开发文档

## 项目概述

多媒体作品评审系统是一个基于 Spring Boot + React 的 B/S 架构 Web 应用，用于组织多媒体作品比赛评选。支持多角色（管理员/评委/选手）协同工作，包含从比赛创建、稿件上传、匿名评分到自动排名的完整工作流。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 |
| 语言 | Java | 17+ |
| 数据库 | H2 (文件持久化) / MySQL | 2.2 |
| ORM | Hibernate / Spring Data JPA | 6.4 |
| 安全 | Spring Security + JWT (jjwt) | - |
| 构建 | Maven | 3.9+ |
| 前端框架 | React | 18 |
| UI 组件库 | Ant Design | 5 |
| 构建工具 | Vite | 6 |
| 语言 | TypeScript | - |

## 项目结构

```
f:/claude_project/
├── backend/                     # Spring Boot 后端 (62 Java 文件)
│   ├── src/main/java/com/multimediareview/
│   │   ├── MultimediaReviewApplication.java
│   │   ├── config/              # 配置 (9 文件)
│   │   │   ├── SecurityConfig.java       # 安全策略 + 角色权限矩阵
│   │   │   ├── JwtAuthFilter.java        # JWT 认证过滤器
│   │   │   ├── JwtTokenProvider.java     # JWT 签发与验证
│   │   │   ├── JwtUserDetails.java       # 用户 Principal
│   │   │   ├── CurrentUser.java          # @CurrentUser 注解
│   │   │   ├── CurrentUserResolver.java  # 参数解析器
│   │   │   ├── DataInitializer.java      # 预置账号初始化
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── WebConfig.java            # CORS
│   │   ├── controller/         # 控制器 (9 文件)
│   │   │   ├── AuthController.java       # 登录/注册
│   │   │   ├── CompetitionController.java # 比赛 CRUD
│   │   │   ├── ParticipantController.java # 参评人管理(管理员)
│   │   │   ├── ParticipantSelfController.java # 选手自服务
│   │   │   ├── JudgeController.java      # 评委分配
│   │   │   ├── ScoreController.java      # 评分提交
│   │   │   ├── ReportController.java     # 排名报表
│   │   │   ├── RescoreController.java    # 复评管理
│   │   │   └── UserController.java       # 用户查询
│   │   ├── dto/                # 数据传输对象 (13 文件)
│   │   ├── entity/             # JPA 实体 (8 文件 + 4 枚举)
│   │   ├── repository/         # Spring Data 仓库 (8 文件)
│   │   ├── scheduler/          # 定时任务(评分锁) (1 文件)
│   │   └── service/            # 业务逻辑 (8 文件)
│   ├── src/main/resources/
│   │   └── application.yml     # 配置
│   └── pom.xml
│
├── frontend/                   # React 前端 (10 TSX/TS 文件)
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx             # 路由配置
│   │   ├── api/client.ts       # Axios 实例 + 拦截器
│   │   └── pages/
│   │       ├── LoginPage.tsx        # 登录/注册
│   │       ├── DashboardPage.tsx     # 仪表盘(比赛列表+创建)
│   │       ├── CompetitionSetupPage.tsx # 比赛配置(参评人/评委)
│   │       ├── ScoringPage.tsx       # 评委评分页
│   │       ├── ReportPage.tsx        # 排名报表+打印
│   │       └── ParticipantWorkspacePage.tsx # 选手工作台
│   └── package.json
│
└── docs/
    ├── DEVELOPMENT.md
    ├── USER_MANUAL.md
    └── superpowers/
        ├── specs/2026-05-10-multimedia-review-system-design.md
        └── plans/2026-05-10-multimedia-review-system-plan.md
```

## 核心架构

### 认证流程

```
[前端登录] → POST /api/auth/login → AuthService → JwtTokenProvider.生成Token
    → 返回 {token, userId, username, name, role}
    → 前端存储到 localStorage
    → 后续请求通过 Axios 拦截器自动附加 Authorization: Bearer <token>
    → JwtAuthFilter 解析 token → 设置 SecurityContext (ROLE_前缀)
```

### 角色权限矩阵

| API 路径模式 | 方法 | 权限 |
|---|---|---|
| `/api/auth/**` | ALL | permitAll |
| `/h2-console/**` | ALL | permitAll |
| `/api/competitions/*/files/**` | GET | permitAll |
| `/api/participant/**` | ALL | PARTICIPANT |
| `/api/competitions/*/scores/**` | ALL | JUDGE |
| `/api/competitions/*/judges/**` | ALL | ADMIN |
| `/api/competitions/*/participants/**` | POST/DELETE | ADMIN |
| `/api/users/**` | ALL | ADMIN |
| `/api/competitions/**` | POST/PUT/DELETE | ADMIN |
| `/api/competitions/**` | GET | authenticated |

### 评分排名算法

1. 收集每位参评人的所有评委有效评分（SUBMITTED / LOCKED）
2. 评委 >= 3 人：去掉最高分和最低分，取剩余分数平均值
3. 评委 < 3 人：所有分数直接取平均值
4. 按平均分降序排列，按名次配置名额依次分配名次
5. 同分自动标记，支持同分复评（创建 RescoreRound）

### 评分锁机制

`ScoreLockScheduler` 定时任务每分钟执行一次，将提交超过配置时限（默认10分钟）的评分自动锁定（SUBMITTED → LOCKED），锁定后不可修改。

## 文件上传

系统支持上传多媒体作品文件，涵盖图片、文档、视频、音频四大类别。

### 支持格式

| 类型 | 扩展名 | 大小限制 |
|------|--------|----------|
| 文档 | txt, doc, docx, pdf, xls, xlsx, ppt, pptx | 10 MB |
| 图片 | jpg, jpeg, png, gif, bmp, webp, svg | 20 MB |
| 视频 | mp4, mov, avi, mkv, wmv, flv, webm | 500 MB |
| 音频 | mp3, wav, aac, flac, ogg, wma | 50 MB |

### 安全策略

`FileStorageService` 内置双层防护：

1. **黑名单拦截** — 40 种可执行文件扩展名直接拒绝（exe, bat, cmd, com, scr, msi, vbs, ps1, sh, py, rb, jar, dll, sys, apk 等）
2. **白名单匹配** — 黑名单通过后，扩展名必须在上述支持列表中，否则拒绝

### 文件预览

`ParticipantController.downloadFile()` 根据文件扩展名返回正确的 MIME 类型和 Content-Disposition 头：

- **图片/PDF/文本/音视频**：`Content-Disposition: inline`（浏览器内预览）
- **其他格式**：`Content-Disposition: attachment`（触发下载）

前端评分页（`ScoringPage`）使用 Modal 弹窗预览，图片用 `<img>` 标签，视频用 `<video>`，音频用 `<audio>`，文档用 `<iframe>`。

技术选型优先 H2 文件数据库，可通过 profile 切换 MySQL。

**H2 配置 (dev):** `jdbc:h2:file:./data/multimediareview`
**控制台:** `http://localhost:8080/h2-console`

### 核心表

| 表 | 说明 | 关键字段 |
|---|---|---|
| users | 用户 | username, password, name, role(20) |
| competitions | 比赛 | name, status, max_rank, total_participants, created_by |
| competition_rank_configs | 名次配置 | rank_number, capacity |
| competition_participants | 参评人 | name, department, user_id, competition_id |
| participant_files | 作品文件 | original_name, file_type(TEXT/IMAGE/VIDEO/AUDIO), file_path |
| competition_judges | 比赛评委 | competition_id, user_id (唯一) |
| scores | 评分 | competition_id, judge_id, participant_id, score(5,1), status |
| rescore_rounds | 复评轮次 | competition_id, round_number |

### 预置账号

| 用户名 | 密码 | 角色 | 姓名 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 系统管理员 |
| judge1 | judge123 | JUDGE | 评委张三 |
| judge2 | judge123 | JUDGE | 评委李四 |
| player1 | player123 | PARTICIPANT | 选手王五 |

## 环境要求

- Java 17 或更高
- Maven 3.9+（手动安装于 `C:\Users\86158\tools\apache-maven-3.9.8`）
- Node.js 18+
- 设置环境变量 `JAVA_OPTS="--enable-native-access=ALL-UNNAMED"`（Java 25 兼容）

## 启动命令

**后端:**
```bash
export JAVA_OPTS="--enable-native-access=ALL-UNNAMED"
export PATH="/c/Users/86158/tools/apache-maven-3.9.8/bin:$PATH"
cd F:/claude_project/backend
java -jar target/multimedia-review-1.0.0.jar --spring.profiles.active=dev
```

**前端:**
```bash
cd F:/claude_project/frontend
npm run dev
```

## API 接口总览

### 认证
- `POST /api/auth/login` — 登录
- `POST /api/auth/register` — 注册

### 比赛管理 (ADMIN)
- `POST /api/competitions` — 创建比赛 → Task 2.1
- `PUT /api/competitions/{id}/start` — 开始比赛
- `PUT /api/competitions/{id}/finish` — 结束比赛

### 参评人管理 (ADMIN)
- `POST /api/competitions/{id}/participants` — 添加参评人
- `GET /api/competitions/{id}/participants` — 参评人列表
- `DELETE /api/competitions/{id}/participants/{pid}` — 删除参评人
- `POST /api/competitions/{id}/participants/{pid}/files` — 上传稿件

### 评委管理 (ADMIN)
- `POST /api/competitions/{id}/judges` — 分配评委
- `GET /api/competitions/{id}/judges` — 评委列表
- `DELETE /api/competitions/{id}/judges/{jid}` — 移除评委

### 评分 (JUDGE)
- `POST /api/competitions/{id}/scores` — 提交评分
- `GET /api/competitions/{id}/my-scores` — 我的评分
- `GET /api/competitions/{id}/all-scores` — 全部评分

### 选手自服务 (PARTICIPANT)
- `GET /api/participant/competitions` — 我的比赛
- `GET /api/participant/competitions/{id}/entries` — 我的条目
- `POST /api/participant/competitions/{id}/files` — 上传我的稿件
- `DELETE /api/participant/files/{fid}` — 删除我的文件
- `GET /api/participant/files/{fid}/download` — 下载文件

### 报表
- `GET /api/competitions/{id}/report` — 排名报表
- `GET /api/competitions/{id}/ties` — 同分检测

### 其他
- `GET /api/users?role=X` — 用户列表(按角色筛选)
- `GET /api/competitions` — 比赛列表(所有角色)
- `GET /api/competitions/{id}` — 比赛详情

## 已知技术问题

1. **Java 25 + Lombok**：不兼容，已全部改用手写 getter/setter/builder
2. **Maven 警告**：Java 25 需 `--enable-native-access=ALL-UNNAMED`
3. **H2 列长度**：`users.role` 已从 10 扩大到 20 以容纳 `PARTICIPANT`
4. **Hibernate 懒加载序列化**：Controller 不能直接返回 JPA Entity，必须转 DTO

## 开发规范

- 所有实体使用手写 Builder（无 Lombok）
- Controller 返回类型统一使用 DTO，不得直接返回 Entity
- DTO 的 equals/hashCode/toString 必须同步更新
- SecurityConfig 中规则顺序：**越具体的规则越靠前**
- 前端路由守卫使用 `PrivateRoute` 组件，支持角色参数
