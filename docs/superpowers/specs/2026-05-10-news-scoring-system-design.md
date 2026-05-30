# 多媒体作品评审系统 — 设计文档

## 概述

多媒体作品评审系统，支持多评委在线匿名评分，自动去除最高最低分计算平均分并排名。名次数量和每个名次的名额可自定义（最多10名），评委数量可自定义。评分过程可追溯，最终生成详细排名报表。

## 技术栈

- **后端**: Java 17, Spring Boot 3.x, Spring Security + JWT, Spring Data JPA (Hibernate)
- **数据库**: MySQL (生产) / H2 (开发)
- **前端**: React 18 + Ant Design 5, Vite 构建
- **通信**: RESTful JSON API, Multipart 文件上传
- **存储**: 本地文件系统 (可配置路径), 生产可切换 OSS/MinIO

## 架构

```
 浏览器 (PC/平板/手机)
   React 18 + Ant Design 5
        │ REST API (JSON)
   Spring Boot 3.x
   ├── 认证模块 (Spring Security + JWT)
   ├── 比赛管理 (CRUD)
   ├── 评分引擎 (去极值 + 排名计算)
   └── 报表模块 (详细评分过程)
        │ JPA
      MySQL
```

## 数据模型

### User
- id, username, password (BCrypt), role (ADMIN/JUDGE), name
- created_at, updated_at

### Competition
- id, name, description
- status (DRAFT / SCORING / FINISHED)
- max_rank (最高名次, 最多10)
- score_modify_window_minutes (默认10)
- created_by (FK → User)
- start_time, end_time, created_at

### CompetitionRankConfig
- id, competition_id (FK), rank_number (1~N)
- capacity (该名次容纳人数)

### CompetitionParticipant
- id, competition_id (FK), name, department

### ParticipantFile (参评稿件文件)
- id, participant_id (FK), file_name, original_name, file_type (TEXT/VIDEO/AUDIO)
- file_path, file_size (bytes), mime_type
- uploaded_at

支持的文件类型和大小限制：
- 文本稿件：.txt, .doc, .docx, .pdf — 最大 10MB
- 视频：.mp4, .mov, .avi, .mkv — 最大 500MB
- 音频：.mp3, .wav, .aac, .flac — 最大 50MB

### CompetitionJudge
- id, competition_id (FK), user_id (FK → User, 该用户必须 role=JUDGE)

### Score
- id, competition_id (FK), judge_id (FK → User), participant_id (FK)
- score (DECIMAL 0~100, 支持小数)
- status (DRAFT / SUBMITTED / LOCKED)
- rescore_round_id (FK, nullable, 关联复评轮次)
- submitted_at, locked_at, created_at, updated_at

### RescoreRound (复评轮次)
- id, competition_id (FK), round_number, reason (同分说明)
- created_at, finished_at

## 核心业务规则

### 评分流程
1. 管理员创建比赛 → 配置名次及名额 → 添加参评人 → 分配评委 → 开始评分
2. 评委独立打分（盲评+匿名，互不可见）
3. 评委提交后 10 分钟内可修改，超时自动锁定
4. 管理员手动结束比赛 → 系统计算排名 → 生成报表

### 评分匿名规则
- 评分阶段：评委看不到其他评委的个人信息和打分
- 结果公布后：报表展示所有评委姓名及对应的详细分数，确保可追溯

### 排名计算
对每个参评人：
1. 收集所有评委的有效评分（仅计算已锁定/已提交的分数）
2. 去掉一个最高分和最一个低分
3. 剩余分数取平均值
4. 按平均分降序排列
5. 按名次容量依次分配排名
6. 同分处理：若多人平均分相同，由管理员发起对该批次同分参评人的重新评分（仅限同分人员参与复评）。复评流程与原评分一致（评委匿名打分，去除最高最低取平均）。复评结果：分数较低者排入原名次的下一个名次（如第1名同分，复评后较低分排入第2名，原第2名顺延）。若复评后仍然同分，则继续复评直至分出高低。复评记录同样纳入评分追溯。

### 分数规则
- 100分制，支持小数（精度保留1位）
- 去掉最高最低分：仅当评委数 ≥ 3 时执行；评委数 ≤ 2 时不去除
    - 若多个评委打出相同最高/最低分，仅去除一个最高和一个最低

### 报表展示
每个参评人展示：
- 原始评分矩阵（评委 × 分数）
- 最高分标红、最低分标蓝
- 去除最高最低后的有效分
- 有效分平均值
- 最终排名

## API 设计（核心端点）

### 认证
- POST /api/auth/login
- POST /api/auth/register (仅管理员)

### 比赛管理 (管理员)
- POST   /api/competitions — 创建比赛
- GET    /api/competitions — 比赛列表
- GET    /api/competitions/{id} — 比赛详情
- PUT    /api/competitions/{id} — 修改比赛
- PUT    /api/competitions/{id}/status — 修改状态 (开始/结束)

### 名次配置 (管理员)
- POST   /api/competitions/{id}/rank-configs — 批量设置名次容量
- GET    /api/competitions/{id}/rank-configs — 查看名次配置

### 参评人管理 (管理员)
- POST   /api/competitions/{id}/participants — 添加参评人
- GET    /api/competitions/{id}/participants — 查看参评人列表
- DELETE /api/competitions/{id}/participants/{pid} — 删除参评人
- POST   /api/competitions/{id}/participants/{pid}/files — 上传稿件文件 (multipart/form-data)
- GET    /api/competitions/{id}/participants/{pid}/files/{fid}/download — 下载/预览稿件

### 评委分配 (管理员)
- POST   /api/competitions/{id}/judges — 分配评委
- GET    /api/competitions/{id}/judges — 查看评委列表

### 评分 (评委 + 管理员查看)
- POST   /api/competitions/{id}/scores — 提交/批量保存评分
- PUT    /api/competitions/{id}/scores/{sid} — 修改评分 (限10分钟内)
- GET    /api/competitions/{id}/my-scores — 评委查看自己的评分
- GET    /api/competitions/{id}/scores — 管理员查看所有评分 (结束前匿名)

### 报表
- GET    /api/competitions/{id}/report — 获取排名报表 (含详细计算过程)

### 复评 (管理员)
- POST   /api/competitions/{id}/rescore — 发起复评 (传入同分参评人ID列表)
- GET    /api/competitions/{id}/rescore-rounds — 查看复评历史

## 权限矩阵

| 操作 | 管理员 | 评委 |
|------|--------|------|
| 创建/管理比赛 | ✓ | ✗ |
| 查看分配的比赛 | ✓ | ✓ (仅自己的) |
| 评委打分 | ✗ | ✓ (仅自己的) |
| 查看其他评委打分 (评分中) | ✗ | ✗ |
| 查看所有打分 (结束前) | ✓ (匿名) | ✗ |
| 查看完整报表 (结束后) | ✓ | ✓ |
| 结束比赛 | ✓ | ✗ |

## 前端页面

1. **登录/注册页** — 账号密码登录
2. **管理员仪表盘** — 比赛列表，创建/管理比赛入口
3. **比赛配置页** — 设置名次容量、添加参评人、分配评委，支持为每个参评人上传稿件文件（文本/视频/音频）
4. **评委评分页** — 参评人列表 + 打分表单 + 稿件预览/播放/下载，提交后可修改（限时），显示倒计时
5. **排名报表页** — 最终排名列表 + 每个参评人的详细计分过程（可展开）
