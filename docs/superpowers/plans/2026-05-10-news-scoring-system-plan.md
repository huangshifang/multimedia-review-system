# 多媒体作品评审系统 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建多媒体作品在线评分系统，支持多评委匿名评分、稿件上传（文本/视频/音频）、去极值排名、同分复评、详细报表。

**Architecture:** Spring Boot 3 后端提供 RESTful API + JWT 认证，React 18 + Ant Design 5 前端，MySQL 持久化，本地文件系统存储稿件。

**Tech Stack:** Java 17, Spring Boot 3.2, Spring Security, JWT (jjwt), JPA/Hibernate, MySQL/H2, React 18, Ant Design 5, Vite, Axios

---

## Phase 1: 后端项目搭建

### Task 1: 创建 Spring Boot 项目骨架

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/multimediareview/MultimediaReviewApplication.java`
- Create: `backend/src/main/resources/application.yml`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>
    <groupId>com.multimediareview</groupId>
    <artifactId>multimedia-review</artifactId>
    <version>1.0.0</version>
    <name>multimedia-review</name>

    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

`backend/src/main/java/com/multimediareview/MultimediaReviewApplication.java`:
```java
package com.multimediareview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MultimediaReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultimediaReviewApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

`backend/src/main/resources/application.yml`:
```yaml
spring:
  profiles:
    active: dev
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 520MB

app:
  jwt:
    secret: ${JWT_SECRET:multimedia-review-secret-key-must-be-at-least-256-bits-long}
    expiration-ms: 86400000
  file:
    upload-dir: ${FILE_UPLOAD_DIR:./uploads}
    allowed-text-extensions: txt,doc,docx,pdf
    allowed-video-extensions: mp4,mov,avi,mkv
    allowed-audio-extensions: mp3,wav,aac,flac
    max-text-size: 10485760
    max-video-size: 524288000
    max-audio-size: 52428800

---
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:multimediareview
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console

---
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:mysql://localhost:3306/multimedia_review?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
```

- [ ] **Step 4: 验证项目能启动**

```bash
cd backend && mvn spring-boot:run
```

Expected: Spring Boot 启动成功，H2 数据库自动初始化。

---

### Task 2: 创建实体类

**Files:**
- Create: `backend/src/main/java/com/multimediareview/entity/User.java`
- Create: `backend/src/main/java/com/multimediareview/entity/Competition.java`
- Create: `backend/src/main/java/com/multimediareview/entity/CompetitionRankConfig.java`
- Create: `backend/src/main/java/com/multimediareview/entity/CompetitionParticipant.java`
- Create: `backend/src/main/java/com/multimediareview/entity/ParticipantFile.java`
- Create: `backend/src/main/java/com/multimediareview/entity/CompetitionJudge.java`
- Create: `backend/src/main/java/com/multimediareview/entity/Score.java`
- Create: `backend/src/main/java/com/multimediareview/entity/RescoreRound.java`
- Create: `backend/src/main/java/com/multimediareview/entity/enums/UserRole.java`
- Create: `backend/src/main/java/com/multimediareview/entity/enums/CompetitionStatus.java`
- Create: `backend/src/main/java/com/multimediareview/entity/enums/ScoreStatus.java`
- Create: `backend/src/main/java/com/multimediareview/entity/enums/FileType.java`

- [ ] **Step 1: 创建枚举类**

`backend/src/main/java/com/multimediareview/entity/enums/UserRole.java`:
```java
package com.multimediareview.entity.enums;

public enum UserRole {
    ADMIN, JUDGE
}
```

`backend/src/main/java/com/multimediareview/entity/enums/CompetitionStatus.java`:
```java
package com.multimediareview.entity.enums;

public enum CompetitionStatus {
    DRAFT, SCORING, FINISHED
}
```

`backend/src/main/java/com/multimediareview/entity/enums/ScoreStatus.java`:
```java
package com.multimediareview.entity.enums;

public enum ScoreStatus {
    DRAFT, SUBMITTED, LOCKED
}
```

`backend/src/main/java/com/multimediareview/entity/enums/FileType.java`:
```java
package com.multimediareview.entity.enums;

public enum FileType {
    TEXT, VIDEO, AUDIO
}
```

- [ ] **Step 2: 创建 User 实体**

`backend/src/main/java/com/multimediareview/entity/User.java`:
```java
package com.multimediareview.entity;

import com.multimediareview.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: 创建 Competition 实体**

`backend/src/main/java/com/multimediareview/entity/Competition.java`:
```java
package com.multimediareview.entity;

import com.multimediareview.entity.enums.CompetitionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"rankConfigs", "participants", "judges"})
@ToString(exclude = {"rankConfigs", "participants", "judges"})
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CompetitionStatus status;

    @Column(nullable = false)
    private Integer maxRank;

    @Column(nullable = false)
    @Builder.Default
    private Integer scoreModifyWindowMinutes = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompetitionRankConfig> rankConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompetitionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompetitionJudge> judges = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = CompetitionStatus.DRAFT;
    }
}
```

- [ ] **Step 4: 创建 CompetitionRankConfig 实体**

`backend/src/main/java/com/multimediareview/entity/CompetitionRankConfig.java`:
```java
package com.multimediareview.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competition_rank_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionRankConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private Integer rankNumber;

    @Column(nullable = false)
    private Integer capacity;
}
```

- [ ] **Step 5: 创建 CompetitionParticipant 实体**

`backend/src/main/java/com/multimediareview/entity/CompetitionParticipant.java`:
```java
package com.multimediareview.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competition_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "files")
@ToString(exclude = "files")
public class CompetitionParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String department;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ParticipantFile> files = new ArrayList<>();
}
```

- [ ] **Step 6: 创建 ParticipantFile 实体**

`backend/src/main/java/com/multimediareview/entity/ParticipantFile.java`:
```java
package com.multimediareview.entity;

import com.multimediareview.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participant_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private CompetitionParticipant participant;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FileType fileType;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 7: 创建 CompetitionJudge 实体**

`backend/src/main/java/com/multimediareview/entity/CompetitionJudge.java`:
```java
package com.multimediareview.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competition_judges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"competition_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionJudge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

- [ ] **Step 8: 创建 Score 实体**

`backend/src/main/java/com/multimediareview/entity/Score.java`:
```java
package com.multimediareview.entity;

import com.multimediareview.entity.enums.ScoreStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"competition_id", "judge_id", "participant_id", "rescore_round_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private CompetitionParticipant participant;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ScoreStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescore_round_id")
    private RescoreRound rescoreRound;

    private LocalDateTime submittedAt;
    private LocalDateTime lockedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ScoreStatus.DRAFT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 9: 创建 RescoreRound 实体**

`backend/src/main/java/com/multimediareview/entity/RescoreRound.java`:
```java
package com.multimediareview.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rescore_rounds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescoreRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private Integer roundNumber;

    @Column(length = 500)
    private String reason;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

---

### Task 3: 创建 JPA Repository 层

**Files:**
- Create: `backend/src/main/java/com/multimediareview/repository/UserRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/CompetitionRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/CompetitionRankConfigRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/CompetitionParticipantRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/ParticipantFileRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/CompetitionJudgeRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/ScoreRepository.java`
- Create: `backend/src/main/java/com/multimediareview/repository/RescoreRoundRepository.java`

- [ ] **Step 1: 创建所有 Repository 接口**

`backend/src/main/java/com/multimediareview/repository/UserRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

`backend/src/main/java/com/multimediareview/repository/CompetitionRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.Competition;
import com.multimediareview.entity.enums.CompetitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByStatusOrderByCreatedAtDesc(CompetitionStatus status);

    @Query("SELECT c FROM Competition c LEFT JOIN FETCH c.rankConfigs WHERE c.id = :id")
    Competition findByIdWithRankConfigs(Long id);
}
```

`backend/src/main/java/com/multimediareview/repository/CompetitionRankConfigRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionRankConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetitionRankConfigRepository extends JpaRepository<CompetitionRankConfig, Long> {
    List<CompetitionRankConfig> findByCompetitionIdOrderByRankNumberAsc(Long competitionId);
}
```

`backend/src/main/java/com/multimediareview/repository/CompetitionParticipantRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetitionParticipantRepository extends JpaRepository<CompetitionParticipant, Long> {
    List<CompetitionParticipant> findByCompetitionId(Long competitionId);
    long countByCompetitionId(Long competitionId);
}
```

`backend/src/main/java/com/multimediareview/repository/ParticipantFileRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.ParticipantFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipantFileRepository extends JpaRepository<ParticipantFile, Long> {
    List<ParticipantFile> findByParticipantId(Long participantId);
    void deleteByParticipantId(Long participantId);
}
```

`backend/src/main/java/com/multimediareview/repository/CompetitionJudgeRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionJudge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CompetitionJudgeRepository extends JpaRepository<CompetitionJudge, Long> {
    List<CompetitionJudge> findByCompetitionId(Long competitionId);
    Optional<CompetitionJudge> findByCompetitionIdAndUserId(Long competitionId, Long userId);
    long countByCompetitionId(Long competitionId);
    boolean existsByCompetitionIdAndUserId(Long competitionId, Long userId);
}
```

`backend/src/main/java/com/multimediareview/repository/ScoreRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.Score;
import com.multimediareview.entity.enums.ScoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);

    List<Score> findByCompetitionIdAndParticipantId(Long competitionId, Long participantId);

    List<Score> findByCompetitionIdAndRescoreRoundId(Long competitionId, Long rescoreRoundId);

    Optional<Score> findByCompetitionIdAndJudgeIdAndParticipantIdAndRescoreRoundIsNull(
            Long competitionId, Long judgeId, Long participantId);

    @Modifying
    @Transactional
    @Query("UPDATE Score s SET s.status = 'LOCKED' WHERE s.status = 'SUBMITTED' AND s.submittedAt < :cutoff")
    int lockScoresOlderThan(LocalDateTime cutoff);
}
```

`backend/src/main/java/com/multimediareview/repository/RescoreRoundRepository.java`:
```java
package com.multimediareview.repository;

import com.multimediareview.entity.RescoreRound;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RescoreRoundRepository extends JpaRepository<RescoreRound, Long> {
    List<RescoreRound> findByCompetitionIdOrderByRoundNumberAsc(Long competitionId);
    long countByCompetitionId(Long competitionId);
}
```

---

## Phase 2: 认证与安全

### Task 4: JWT 工具类与安全配置

**Files:**
- Create: `backend/src/main/java/com/multimediareview/config/JwtTokenProvider.java`
- Create: `backend/src/main/java/com/multimediareview/config/JwtAuthFilter.java`
- Create: `backend/src/main/java/com/multimediareview/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/multimediareview/config/WebConfig.java`

- [ ] **Step 1: 创建 JwtTokenProvider**

`backend/src/main/java/com/multimediareview/config/JwtTokenProvider.java`:
```java
package com.multimediareview.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

- [ ] **Step 2: 创建 JwtAuthFilter**

`backend/src/main/java/com/multimediareview/config/JwtAuthFilter.java`:
```java
package com.multimediareview.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token)) {
            try {
                Claims claims = jwtTokenProvider.validateToken(token);
                Long userId = Long.parseLong(claims.getSubject());
                String username = claims.get("username", String.class);
                String role = claims.get("role", String.class);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                new JwtUserDetails(userId, username),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // token invalid, proceed without auth
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 3: 创建 JwtUserDetails**

`backend/src/main/java/com/multimediareview/config/JwtUserDetails.java`:
```java
package com.multimediareview.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserDetails {
    private Long userId;
    private String username;
}
```

- [ ] **Step 4: 创建 SecurityConfig**

`backend/src/main/java/com/multimediareview/config/SecurityConfig.java`:
```java
package com.multimediareview.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/competitions/*/files/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(fo -> fo.disable()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- [ ] **Step 5: 创建 WebConfig (CORS)**

`backend/src/main/java/com/multimediareview/config/WebConfig.java`:
```java
package com.multimediareview.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

### Task 5: 认证服务与控制器

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/RegisterRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/AuthResponse.java`
- Create: `backend/src/main/java/com/multimediareview/service/AuthService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/AuthController.java`
- Create: `backend/src/main/java/com/multimediareview/config/CurrentUser.java`

- [ ] **Step 1: 创建 DTO**

`backend/src/main/java/com/multimediareview/dto/LoginRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
```

`backend/src/main/java/com/multimediareview/dto/RegisterRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50)
    private String username;
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String role; // ADMIN or JUDGE
}
```

`backend/src/main/java/com/multimediareview/dto/AuthResponse.java`:
```java
package com.multimediareview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String name;
    private String role;
}
```

- [ ] **Step 2: 创建 CurrentUser 注解**

`backend/src/main/java/com/multimediareview/config/CurrentUser.java`:
```java
package com.multimediareview.config;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
```

- [ ] **Step 3: 创建 CurrentUserResolver**

`backend/src/main/java/com/multimediareview/config/CurrentUserResolver.java`:
```java
package com.multimediareview.config;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
```

在 `WebConfig` 中注册 resolver:
```java
// 在 WebConfig.java 中添加:
@Autowired
private CurrentUserResolver currentUserResolver;

@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(currentUserResolver);
}
```

- [ ] **Step 4: 创建 AuthService**

`backend/src/main/java/com/multimediareview/service/AuthService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.config.JwtTokenProvider;
import com.multimediareview.dto.AuthResponse;
import com.multimediareview.dto.LoginRequest;
import com.multimediareview.dto.RegisterRequest;
import com.multimediareview.entity.User;
import com.multimediareview.entity.enums.UserRole;
import com.multimediareview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .build();
        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return toAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return toAuthResponse(user, token);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
```

- [ ] **Step 5: 创建 AuthController**

`backend/src/main/java/com/multimediareview/controller/AuthController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.dto.AuthResponse;
import com.multimediareview.dto.LoginRequest;
import com.multimediareview.dto.RegisterRequest;
import com.multimediareview.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
```

---

## Phase 3: 比赛管理与文件上传

### Task 6: 比赛服务与控制器

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/CompetitionCreateRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/CompetitionResponse.java`
- Create: `backend/src/main/java/com/multimediareview/dto/RankConfigRequest.java`
- Create: `backend/src/main/java/com/multimediareview/service/CompetitionService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/CompetitionController.java`

- [ ] **Step 1: 创建 DTO**

`backend/src/main/java/com/multimediareview/dto/CompetitionCreateRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CompetitionCreateRequest {
    @NotBlank
    private String name;
    private String description;

    @Min(1) @Max(10)
    private Integer maxRank;

    @Min(1) @Max(60)
    private Integer scoreModifyWindowMinutes;

    @NotEmpty
    private List<RankConfigItem> rankConfigs;

    @Data
    public static class RankConfigItem {
        @Min(1) @Max(10)
        private Integer rankNumber;
        @Min(1)
        private Integer capacity;
    }
}
```

`backend/src/main/java/com/multimediareview/dto/CompetitionResponse.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CompetitionResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer maxRank;
    private Integer scoreModifyWindowMinutes;
    private String createdByName;
    private Long participantCount;
    private Long judgeCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private List<RankConfigResponse> rankConfigs;

    @Data
    @Builder
    public static class RankConfigResponse {
        private Long id;
        private Integer rankNumber;
        private Integer capacity;
    }
}
```

- [ ] **Step 2: 创建 CompetitionService**

`backend/src/main/java/com/multimediareview/service/CompetitionService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.CompetitionCreateRequest;
import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.CompetitionStatus;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionRankConfigRepository rankConfigRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final CompetitionJudgeRepository judgeRepository;

    @Transactional
    public CompetitionResponse create(CompetitionCreateRequest request, User creator) {
        int totalCapacity = request.getRankConfigs().stream()
                .mapToInt(CompetitionCreateRequest.RankConfigItem::getCapacity).sum();

        Competition competition = Competition.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxRank(request.getMaxRank())
                .scoreModifyWindowMinutes(request.getScoreModifyWindowMinutes() != null
                        ? request.getScoreModifyWindowMinutes() : 10)
                .status(CompetitionStatus.DRAFT)
                .createdBy(creator)
                .build();

        competition = competitionRepository.save(competition);

        for (var rc : request.getRankConfigs()) {
            CompetitionRankConfig config = CompetitionRankConfig.builder()
                    .competition(competition)
                    .rankNumber(rc.getRankNumber())
                    .capacity(rc.getCapacity())
                    .build();
            rankConfigRepository.save(config);
        }

        return toResponse(competition);
    }

    public List<CompetitionResponse> listAll() {
        return competitionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CompetitionResponse getById(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        return toResponse(c);
    }

    @Transactional
    public CompetitionResponse startScoring(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (c.getStatus() != CompetitionStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的比赛可以开始");
        }
        c.setStatus(CompetitionStatus.SCORING);
        c.setStartTime(LocalDateTime.now());
        return toResponse(competitionRepository.save(c));
    }

    @Transactional
    public CompetitionResponse finish(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (c.getStatus() != CompetitionStatus.SCORING) {
            throw new RuntimeException("只有评分中的比赛可以结束");
        }
        c.setStatus(CompetitionStatus.FINISHED);
        c.setEndTime(LocalDateTime.now());
        return toResponse(competitionRepository.save(c));
    }

    private CompetitionResponse toResponse(Competition c) {
        List<CompetitionRankConfig> configs = rankConfigRepository
                .findByCompetitionIdOrderByRankNumberAsc(c.getId());

        return CompetitionResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .status(c.getStatus().name())
                .maxRank(c.getMaxRank())
                .scoreModifyWindowMinutes(c.getScoreModifyWindowMinutes())
                .createdByName(c.getCreatedBy().getName())
                .participantCount(participantRepository.countByCompetitionId(c.getId()))
                .judgeCount(judgeRepository.countByCompetitionId(c.getId()))
                .startTime(c.getStartTime())
                .endTime(c.getEndTime())
                .createdAt(c.getCreatedAt())
                .rankConfigs(configs.stream()
                        .map(rc -> CompetitionResponse.RankConfigResponse.builder()
                                .id(rc.getId())
                                .rankNumber(rc.getRankNumber())
                                .capacity(rc.getCapacity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
```

- [ ] **Step 3: 创建 CompetitionController**

`backend/src/main/java/com/multimediareview/controller/CompetitionController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.CompetitionCreateRequest;
import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.entity.User;
import com.multimediareview.repository.UserRepository;
import com.multimediareview.service.CompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<CompetitionResponse> create(@Valid @RequestBody CompetitionCreateRequest request,
                                                       @CurrentUser JwtUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(competitionService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> list() {
        return ResponseEntity.ok(competitionService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<CompetitionResponse> startScoring(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.startScoring(id));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<CompetitionResponse> finish(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.finish(id));
    }
}
```

---

### Task 7: 参评人管理与文件上传

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/ParticipantRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/ParticipantResponse.java`
- Create: `backend/src/main/java/com/multimediareview/service/FileStorageService.java`
- Create: `backend/src/main/java/com/multimediareview/service/ParticipantService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/ParticipantController.java`

- [ ] **Step 1: 创建空DTO（后续填充）**

`backend/src/main/java/com/multimediareview/dto/ParticipantRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParticipantRequest {
    @NotBlank
    private String name;
    private String department;
}
```

`backend/src/main/java/com/multimediareview/dto/ParticipantResponse.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ParticipantResponse {
    private Long id;
    private String name;
    private String department;
    private List<FileResponse> files;

    @Data
    @Builder
    public static class FileResponse {
        private Long id;
        private String originalName;
        private String fileType;
        private Long fileSize;
        private String downloadUrl;
    }
}
```

- [ ] **Step 2: 创建 FileStorageService**

`backend/src/main/java/com/multimediareview/service/FileStorageService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.entity.ParticipantFile;
import com.multimediareview.entity.enums.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir;
    private final Set<String> textExts, videoExts, audioExts;
    private final long maxTextSize, maxVideoSize, maxAudioSize;

    public FileStorageService(
            @Value("${app.file.upload-dir}") String uploadDir,
            @Value("${app.file.allowed-text-extensions}") String textExts,
            @Value("${app.file.allowed-video-extensions}") String videoExts,
            @Value("${app.file.allowed-audio-extensions}") String audioExts,
            @Value("${app.file.max-text-size}") long maxTextSize,
            @Value("${app.file.max-video-size}") long maxVideoSize,
            @Value("${app.file.max-audio-size}") long maxAudioSize) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.textExts = new HashSet<>(Arrays.asList(textExts.split(",")));
        this.videoExts = new HashSet<>(Arrays.asList(videoExts.split(",")));
        this.audioExts = new HashSet<>(Arrays.asList(audioExts.split(",")));
        this.maxTextSize = maxTextSize;
        this.maxVideoSize = maxVideoSize;
        this.maxAudioSize = maxAudioSize;
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    public ParticipantFile store(MultipartFile file, Long participantId) {
        String originalName = file.getOriginalFilename();
        String ext = getExtension(originalName).toLowerCase();
        FileType fileType = detectFileType(ext);
        validateSize(file.getSize(), fileType);

        String storedName = UUID.randomUUID().toString() + "." + ext;
        Path subDir = uploadDir.resolve(participantId.toString());
        try {
            Files.createDirectories(subDir);
            Path target = subDir.resolve(storedName);
            file.transferTo(target);

            return ParticipantFile.builder()
                    .fileName(storedName)
                    .originalName(originalName)
                    .fileType(fileType)
                    .filePath(target.toString())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
    }

    public Resource loadFile(ParticipantFile file) {
        try {
            Path path = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) return resource;
            throw new RuntimeException("文件不存在");
        } catch (Exception e) {
            throw new RuntimeException("文件加载失败", e);
        }
    }

    public void deleteFile(ParticipantFile file) {
        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            log.warn("删除文件失败: {}", file.getFilePath());
        }
    }

    private FileType detectFileType(String ext) {
        if (textExts.contains(ext)) return FileType.TEXT;
        if (videoExts.contains(ext)) return FileType.VIDEO;
        if (audioExts.contains(ext)) return FileType.AUDIO;
        throw new RuntimeException("不支持的文件类型: " + ext);
    }

    private void validateSize(long size, FileType type) {
        long max = switch (type) {
            case TEXT -> maxTextSize;
            case VIDEO -> maxVideoSize;
            case AUDIO -> maxAudioSize;
        };
        if (size > max) {
            throw new RuntimeException(String.format("文件大小超过限制: %d MB", max / 1048576));
        }
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i < 0) throw new RuntimeException("无法识别文件类型");
        return fileName.substring(i + 1);
    }
}
```

- [ ] **Step 3: 创建 ParticipantService**

`backend/src/main/java/com/multimediareview/service/ParticipantService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.ParticipantRequest;
import com.multimediareview.dto.ParticipantResponse;
import com.multimediareview.entity.*;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final CompetitionParticipantRepository participantRepository;
    private final ParticipantFileRepository fileRepository;
    private final CompetitionRepository competitionRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ParticipantResponse addParticipant(Long competitionId, ParticipantRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        CompetitionParticipant participant = CompetitionParticipant.builder()
                .competition(competition)
                .name(request.getName())
                .department(request.getDepartment())
                .build();

        return toResponse(participantRepository.save(participant));
    }

    public List<ParticipantResponse> listParticipants(Long competitionId) {
        return participantRepository.findByCompetitionId(competitionId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipantResponse uploadFile(Long competitionId, Long participantId, MultipartFile file) {
        CompetitionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("参评人不存在"));

        ParticipantFile pf = fileStorageService.store(file, participantId);
        pf.setParticipant(participant);
        pf = fileRepository.save(pf);

        return toResponse(participant);
    }

    @Transactional
    public void deleteParticipant(Long competitionId, Long participantId) {
        CompetitionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("参评人不存在"));
        for (ParticipantFile f : participant.getFiles()) {
            fileStorageService.deleteFile(f);
        }
        participantRepository.delete(participant);
    }

    public ParticipantFile getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
    }

    private ParticipantResponse toResponse(CompetitionParticipant p) {
        List<ParticipantFile> files = fileRepository.findByParticipantId(p.getId());
        return ParticipantResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .department(p.getDepartment())
                .files(files.stream().map(f -> ParticipantResponse.FileResponse.builder()
                        .id(f.getId())
                        .originalName(f.getOriginalName())
                        .fileType(f.getFileType().name())
                        .fileSize(f.getFileSize())
                        .downloadUrl("/api/competitions/" + p.getCompetition().getId()
                                + "/files/" + f.getId() + "/download")
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
```

- [ ] **Step 4: 创建 ParticipantController**

`backend/src/main/java/com/multimediareview/controller/ParticipantController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.dto.ParticipantRequest;
import com.multimediareview.dto.ParticipantResponse;
import com.multimediareview.entity.ParticipantFile;
import com.multimediareview.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/participants")
    public ResponseEntity<ParticipantResponse> add(@PathVariable Long competitionId,
                                                    @Valid @RequestBody ParticipantRequest request) {
        return ResponseEntity.ok(participantService.addParticipant(competitionId, request));
    }

    @GetMapping("/participants")
    public ResponseEntity<List<ParticipantResponse>> list(@PathVariable Long competitionId) {
        return ResponseEntity.ok(participantService.listParticipants(competitionId));
    }

    @PostMapping("/participants/{pid}/files")
    public ResponseEntity<ParticipantResponse> uploadFile(@PathVariable Long competitionId,
                                                           @PathVariable Long pid,
                                                           @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(participantService.uploadFile(competitionId, pid, file));
    }

    @DeleteMapping("/participants/{pid}")
    public ResponseEntity<Void> delete(@PathVariable Long competitionId,
                                       @PathVariable Long pid) {
        participantService.deleteParticipant(competitionId, pid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/{fid}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long competitionId,
                                                  @PathVariable Long fid) {
        ParticipantFile pf = participantService.getFile(fid);
        Resource resource = participantService.getFile(fid);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + pf.getOriginalName() + "\"")
                .body(resource);
    }
}
```

需要给 `ParticipantService` 添加 `getFile` 返回 Resource 的重载方法:
在 `ParticipantService` 中补充:
```java
import org.springframework.core.io.Resource;

public Resource getFile(Long fileId) {
    ParticipantFile pf = fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("文件不存在"));
    return fileStorageService.loadFile(pf);
}
```

修正 controller 中重复调用 getFile 的问题 — 改为直接获取信息后分别调用:
```java
@GetMapping("/files/{fid}/download")
public ResponseEntity<Resource> downloadFile(@PathVariable Long competitionId,
                                              @PathVariable Long fid) {
    ParticipantFile pf = participantService.getFileInfo(fid);
    Resource resource = participantService.loadFileResource(fid);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + pf.getOriginalName() + "\"")
            .body(resource);
}
```

在 `ParticipantService` 中拆分为两个方法:
```java
public ParticipantFile getFileInfo(Long fileId) {
    return fileRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("文件不存在"));
}

public Resource loadFileResource(Long fileId) {
    ParticipantFile pf = getFileInfo(fileId);
    return fileStorageService.loadFile(pf);
}
```

---

### Task 8: 评委分配

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/JudgeAssignmentRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/JudgeResponse.java`
- Create: `backend/src/main/java/com/multimediareview/service/JudgeService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/JudgeController.java`

- [ ] **Step 1: 创建 DTO**

`backend/src/main/java/com/multimediareview/dto/JudgeAssignmentRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class JudgeAssignmentRequest {
    @NotEmpty
    private List<Long> userIds;
}
```

`backend/src/main/java/com/multimediareview/dto/JudgeResponse.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JudgeResponse {
    private Long id;
    private Long userId;
    private String username;
    private String name;
}
```

- [ ] **Step 2: 创建 JudgeService**

`backend/src/main/java/com/multimediareview/service/JudgeService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.JudgeResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.UserRole;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JudgeService {

    private final CompetitionJudgeRepository judgeRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<JudgeResponse> assignJudges(Long competitionId, List<Long> userIds) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        for (Long uid : userIds) {
            User user = userRepository.findById(uid)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + uid));
            if (user.getRole() != UserRole.JUDGE) {
                throw new RuntimeException("用户 " + user.getName() + " 不是评委角色");
            }
            if (!judgeRepository.existsByCompetitionIdAndUserId(competitionId, uid)) {
                judgeRepository.save(CompetitionJudge.builder()
                        .competition(competition)
                        .user(user)
                        .build());
            }
        }
        return listJudges(competitionId);
    }

    public List<JudgeResponse> listJudges(Long competitionId) {
        return judgeRepository.findByCompetitionId(competitionId).stream()
                .map(j -> JudgeResponse.builder()
                        .id(j.getId())
                        .userId(j.getUser().getId())
                        .username(j.getUser().getUsername())
                        .name(j.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeJudge(Long competitionId, Long judgeAssignmentId) {
        CompetitionJudge judge = judgeRepository.findById(judgeAssignmentId)
                .orElseThrow(() -> new RuntimeException("评委分配不存在"));
        judgeRepository.delete(judge);
    }
}
```

- [ ] **Step 3: 创建 JudgeController**

`backend/src/main/java/com/multimediareview/controller/JudgeController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.dto.JudgeAssignmentRequest;
import com.multimediareview.dto.JudgeResponse;
import com.multimediareview.service.JudgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}/judges")
@RequiredArgsConstructor
public class JudgeController {

    private final JudgeService judgeService;

    @PostMapping
    public ResponseEntity<List<JudgeResponse>> assign(@PathVariable Long competitionId,
                                                       @Valid @RequestBody JudgeAssignmentRequest request) {
        return ResponseEntity.ok(judgeService.assignJudges(competitionId, request.getUserIds()));
    }

    @GetMapping
    public ResponseEntity<List<JudgeResponse>> list(@PathVariable Long competitionId) {
        return ResponseEntity.ok(judgeService.listJudges(competitionId));
    }

    @DeleteMapping("/{judgeId}")
    public ResponseEntity<Void> remove(@PathVariable Long competitionId,
                                        @PathVariable Long judgeId) {
        judgeService.removeJudge(competitionId, judgeId);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Phase 4: 评分与排名引擎

### Task 9: 评分服务

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/ScoreSubmitRequest.java`
- Create: `backend/src/main/java/com/multimediareview/dto/ScoreResponse.java`
- Create: `backend/src/main/java/com/multimediareview/service/ScoreService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/ScoreController.java`

- [ ] **Step 1: 创建 DTO**

`backend/src/main/java/com/multimediareview/dto/ScoreSubmitRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ScoreSubmitRequest {
    @NotEmpty
    private List<ScoreItem> scores;

    @Data
    public static class ScoreItem {
        @NotNull
        private Long participantId;
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
        private BigDecimal score;
    }
}
```

`backend/src/main/java/com/multimediareview/dto/ScoreResponse.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ScoreResponse {
    private Long id;
    private Long participantId;
    private String participantName;
    private BigDecimal score;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime lockedAt;
}
```

- [ ] **Step 2: 创建 ScoreService**

`backend/src/main/java/com/multimediareview/service/ScoreService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.ScoreResponse;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.ScoreStatus;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final CompetitionJudgeRepository judgeRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<ScoreResponse> submitScores(Long competitionId, Long judgeId,
                                             ScoreSubmitRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (!competition.getStatus().name().equals("SCORING")) {
            throw new RuntimeException("比赛不在评分阶段");
        }

        User judge = userRepository.findById(judgeId)
                .orElseThrow(() -> new RuntimeException("评委不存在"));

        for (var item : request.getScores()) {
            if (item.getScore().compareTo(java.math.BigDecimal.valueOf(100)) > 0
                    || item.getScore().compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new RuntimeException("分数必须在0-100之间");
            }

            Optional<Score> existing = scoreRepository
                    .findByCompetitionIdAndJudgeIdAndParticipantIdAndRescoreRoundIsNull(
                            competitionId, judgeId, item.getParticipantId());

            Score score;
            if (existing.isPresent()) {
                score = existing.get();
                if (score.getStatus() == ScoreStatus.LOCKED) {
                    throw new RuntimeException("评分已锁定无法修改");
                }
                score.setScore(item.getScore());
                score.setStatus(ScoreStatus.SUBMITTED);
                score.setSubmittedAt(LocalDateTime.now());
            } else {
                CompetitionParticipant participant = participantRepository
                        .findById(item.getParticipantId())
                        .orElseThrow(() -> new RuntimeException("参评人不存在"));
                score = Score.builder()
                        .competition(competition)
                        .judge(judge)
                        .participant(participant)
                        .score(item.getScore())
                        .status(ScoreStatus.SUBMITTED)
                        .submittedAt(LocalDateTime.now())
                        .build();
            }
            scoreRepository.save(score);
        }

        return getMyScores(competitionId, judgeId);
    }

    public List<ScoreResponse> getMyScores(Long competitionId, Long judgeId) {
        return scoreRepository.findByCompetitionIdAndJudgeId(competitionId, judgeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ScoreResponse> getAllScores(Long competitionId) {
        return scoreRepository.findByCompetitionIdAndJudgeId(competitionId, null).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ScoreResponse toResponse(Score s) {
        return ScoreResponse.builder()
                .id(s.getId())
                .participantId(s.getParticipant().getId())
                .participantName(s.getParticipant().getName())
                .score(s.getScore())
                .status(s.getStatus().name())
                .submittedAt(s.getSubmittedAt())
                .lockedAt(s.getLockedAt())
                .build();
    }
}
```

- [ ] **Step 3: 创建 ScoreController**

`backend/src/main/java/com/multimediareview/controller/ScoreController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.ScoreResponse;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.service.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping("/scores")
    public ResponseEntity<List<ScoreResponse>> submit(@PathVariable Long competitionId,
                                                       @CurrentUser JwtUserDetails userDetails,
                                                       @Valid @RequestBody ScoreSubmitRequest request) {
        return ResponseEntity.ok(scoreService.submitScores(competitionId, userDetails.getUserId(), request));
    }

    @GetMapping("/my-scores")
    public ResponseEntity<List<ScoreResponse>> myScores(@PathVariable Long competitionId,
                                                         @CurrentUser JwtUserDetails userDetails) {
        return ResponseEntity.ok(scoreService.getMyScores(competitionId, userDetails.getUserId()));
    }

    @GetMapping("/all-scores")
    public ResponseEntity<List<ScoreResponse>> allScores(@PathVariable Long competitionId) {
        return ResponseEntity.ok(scoreService.getAllScores(competitionId));
    }
}
```

---

### Task 10: 评分锁定定时任务

**Files:**
- Create: `backend/src/main/java/com/multimediareview/scheduler/ScoreLockScheduler.java`

- [ ] **Step 1: 创建 ScoreLockScheduler**

`backend/src/main/java/com/multimediareview/scheduler/ScoreLockScheduler.java`:
```java
package com.multimediareview.scheduler;

import com.multimediareview.entity.Competition;
import com.multimediareview.entity.enums.CompetitionStatus;
import com.multimediareview.repository.CompetitionRepository;
import com.multimediareview.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreLockScheduler {

    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    @Transactional
    public void lockExpiredScores() {
        List<Competition> activeCompetitions = competitionRepository
                .findByStatusOrderByCreatedAtDesc(CompetitionStatus.SCORING);

        for (Competition c : activeCompetitions) {
            LocalDateTime cutoff = LocalDateTime.now()
                    .minusMinutes(c.getScoreModifyWindowMinutes());
            int locked = scoreRepository.lockScoresOlderThan(cutoff);
            if (locked > 0) {
                log.info("锁定比赛 [{}] 的 {} 条过期评分", c.getId(), locked);
            }
        }
    }
}
```

---

### Task 11: 排名计算引擎

**Files:**
- Create: `backend/src/main/java/com/multimediareview/service/RankingService.java`
- Create: `backend/src/main/java/com/multimediareview/dto/ReportResponse.java`

- [ ] **Step 1: 创建 ReportResponse DTO**

`backend/src/main/java/com/multimediareview/dto/ReportResponse.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReportResponse {
    private Long competitionId;
    private String competitionName;
    private String status;
    private List<RankResult> ranks;

    @Data
    @Builder
    public static class RankResult {
        private Integer rankNumber;
        private String rankLabel; // "第1名"
        private List<ParticipantScoreDetail> participants;
    }

    @Data
    @Builder
    public static class ParticipantScoreDetail {
        private Long participantId;
        private String participantName;
        private String department;
        private BigDecimal averageScore;
        private List<JudgeScoreDetail> judgeScores;
        private BigDecimal removedHighest;
        private BigDecimal removedLowest;
        private List<BigDecimal> effectiveScores;
        private String calculationProcess; // 计算过程描述
    }

    @Data
    @Builder
    public static class JudgeScoreDetail {
        private Long judgeId;
        private String judgeName;
        private BigDecimal score;
        private boolean isHighest;
        private boolean isLowest;
    }
}
```

- [ ] **Step 2: 创建 RankingService**

`backend/src/main/java/com/multimediareview/service/RankingService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.ReportResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.CompetitionStatus;
import com.multimediareview.entity.enums.ScoreStatus;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final CompetitionJudgeRepository judgeRepository;
    private final CompetitionRankConfigRepository rankConfigRepository;
    private final ScoreRepository scoreRepository;

    public ReportResponse generateReport(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        List<CompetitionParticipant> participants = participantRepository
                .findByCompetitionId(competitionId);
        List<CompetitionRankConfig> rankConfigs = rankConfigRepository
                .findByCompetitionIdOrderByRankNumberAsc(competitionId);

        List<ReportResponse.ParticipantScoreDetail> details = new ArrayList<>();
        for (CompetitionParticipant p : participants) {
            List<Score> scores = scoreRepository
                    .findByCompetitionIdAndParticipantId(competitionId, p.getId())
                    .stream()
                    .filter(s -> s.getRescoreRound() == null
                            && (s.getStatus() == ScoreStatus.SUBMITTED || s.getStatus() == ScoreStatus.LOCKED))
                    .collect(Collectors.toList());

            if (scores.isEmpty()) continue;

            ReportResponse.ParticipantScoreDetail detail = calculateScoreDetail(p, scores);
            details.add(detail);
        }

        // 按平均分降序排列
        details.sort((a, b) -> b.getAverageScore().compareTo(a.getAverageScore()));

        // 按名次容量分配排名
        List<ReportResponse.RankResult> ranks = assignRanks(details, rankConfigs);

        return ReportResponse.builder()
                .competitionId(competition.getId())
                .competitionName(competition.getName())
                .status(competition.getStatus().name())
                .ranks(ranks)
                .build();
    }

    private ReportResponse.ParticipantScoreDetail calculateScoreDetail(
            CompetitionParticipant participant, List<Score> scores) {

        List<Score> sorted = scores.stream()
                .sorted(Comparator.comparing(Score::getScore))
                .collect(Collectors.toList());

        BigDecimal highest = sorted.get(sorted.size() - 1).getScore();
        BigDecimal lowest = sorted.get(0).getScore();

        List<BigDecimal> effectiveScores;
        BigDecimal average;
        StringBuilder process = new StringBuilder();

        if (sorted.size() >= 3) {
            process.append("原始分(").append(sorted.size()).append("个): ");
            process.append(sorted.stream().map(s -> s.getScore().toString())
                    .collect(Collectors.joining(", ")));
            process.append(" → 去掉最高分").append(highest)
                    .append("和最低分").append(lowest);

            effectiveScores = sorted.subList(1, sorted.size() - 1).stream()
                    .map(Score::getScore)
                    .collect(Collectors.toList());

            BigDecimal sum = effectiveScores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            average = sum.divide(BigDecimal.valueOf(effectiveScores.size()), 1, RoundingMode.HALF_UP);

            process.append(" → 有效分(").append(effectiveScores.size()).append("个): ")
                    .append(effectiveScores.stream().map(BigDecimal::toString)
                            .collect(Collectors.joining(", ")))
                    .append(" → 平均分 = ").append(average);
        } else {
            effectiveScores = sorted.stream().map(Score::getScore)
                    .collect(Collectors.toList());
            BigDecimal sum = effectiveScores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            average = sum.divide(BigDecimal.valueOf(effectiveScores.size()), 1, RoundingMode.HALF_UP);
            process.append("评委不足3人，不去极值 → 原始分取平均 = ").append(average);
        }

        List<ReportResponse.JudgeScoreDetail> judgeDetails = sorted.stream()
                .map(s -> ReportResponse.JudgeScoreDetail.builder()
                        .judgeId(s.getJudge().getId())
                        .judgeName(s.getJudge().getName())
                        .score(s.getScore())
                        .isHighest(sorted.size() >= 3 && s.getScore().compareTo(highest) == 0)
                        .isLowest(sorted.size() >= 3 && s.getScore().compareTo(lowest) == 0)
                        .build())
                .collect(Collectors.toList());

        return ReportResponse.ParticipantScoreDetail.builder()
                .participantId(participant.getId())
                .participantName(participant.getName())
                .department(participant.getDepartment())
                .averageScore(average)
                .judgeScores(judgeDetails)
                .removedHighest(sorted.size() >= 3 ? highest : null)
                .removedLowest(sorted.size() >= 3 ? lowest : null)
                .effectiveScores(effectiveScores)
                .calculationProcess(process.toString())
                .build();
    }

    private List<ReportResponse.RankResult> assignRanks(
            List<ReportResponse.ParticipantScoreDetail> sortedDetails,
            List<CompetitionRankConfig> rankConfigs) {

        List<ReportResponse.RankResult> results = new ArrayList<>();
        int idx = 0;
        for (CompetitionRankConfig config : rankConfigs) {
            List<ReportResponse.ParticipantScoreDetail> rankParticipants = new ArrayList<>();
            for (int i = 0; i < config.getCapacity() && idx < sortedDetails.size(); i++, idx++) {
                rankParticipants.add(sortedDetails.get(idx));
            }
            results.add(ReportResponse.RankResult.builder()
                    .rankNumber(config.getRankNumber())
                    .rankLabel("第" + config.getRankNumber() + "名")
                    .participants(rankParticipants)
                    .build());
        }
        return results;
    }

    /** 检测同分情况 */
    public List<List<ReportResponse.ParticipantScoreDetail>> findTies(Long competitionId) {
        ReportResponse report = generateReport(competitionId);
        List<List<ReportResponse.ParticipantScoreDetail>> tieGroups = new ArrayList<>();

        for (ReportResponse.RankResult rank : report.getRanks()) {
            Map<BigDecimal, List<ReportResponse.ParticipantScoreDetail>> byScore = new HashMap<>();
            for (ReportResponse.ParticipantScoreDetail p : rank.getParticipants()) {
                byScore.computeIfAbsent(p.getAverageScore(), k -> new ArrayList<>()).add(p);
            }
            for (var entry : byScore.entrySet()) {
                if (entry.getValue().size() > 1) {
                    tieGroups.add(entry.getValue());
                }
            }
        }
        return tieGroups;
    }
}
```

---

### Task 12: 复评服务

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/RescoreRequest.java`
- Create: `backend/src/main/java/com/multimediareview/service/RescoreService.java`
- Create: `backend/src/main/java/com/multimediareview/controller/RescoreController.java`

- [ ] **Step 1: 创建 RescoreRequest DTO**

`backend/src/main/java/com/multimediareview/dto/RescoreRequest.java`:
```java
package com.multimediareview.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class RescoreRequest {
    @NotEmpty
    private List<Long> participantIds;
    private String reason;
}
```

- [ ] **Step 2: 创建 RescoreService**

`backend/src/main/java/com/multimediareview/service/RescoreService.java`:
```java
package com.multimediareview.service;

import com.multimediareview.dto.RescoreRequest;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.*;
import com.multimediareview.enums.ScoreStatus;
import com.multimediareview.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RescoreService {

    private final RescoreRoundRepository rescoreRoundRepository;
    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionJudgeRepository judgeRepository;
    private final CompetitionParticipantRepository participantRepository;

    @Transactional
    public RescoreRound initiateRescore(Long competitionId, RescoreRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        long count = rescoreRoundRepository.countByCompetitionId(competitionId);
        RescoreRound round = RescoreRound.builder()
                .competition(competition)
                .roundNumber((int) (count + 1))
                .reason(request.getReason())
                .build();
        return rescoreRoundRepository.save(round);
    }

    @Transactional
    public void submitRescoreScores(Long competitionId, Long roundId, Long judgeId,
                                     ScoreSubmitRequest request) {
        RescoreRound round = rescoreRoundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("复评轮次不存在"));

        User judge = new User();
        judge.setId(judgeId);

        for (var item : request.getScores()) {
            CompetitionParticipant participant = participantRepository
                    .findById(item.getParticipantId())
                    .orElseThrow(() -> new RuntimeException("参评人不存在"));

            Score score = Score.builder()
                    .competition(round.getCompetition())
                    .judge(judge)
                    .participant(participant)
                    .score(item.getScore())
                    .status(ScoreStatus.SUBMITTED)
                    .rescoreRound(round)
                    .build();
            scoreRepository.save(score);
        }
    }

    public List<RescoreRound> getRounds(Long competitionId) {
        return rescoreRoundRepository.findByCompetitionIdOrderByRoundNumberAsc(competitionId);
    }
}
```

- [ ] **Step 3: 创建 RescoreController**

`backend/src/main/java/com/multimediareview/controller/RescoreController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.RescoreRequest;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.RescoreRound;
import com.multimediareview.service.RescoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
@RequiredArgsConstructor
public class RescoreController {

    private final RescoreService rescoreService;

    @PostMapping("/rescore")
    public ResponseEntity<RescoreRound> initiate(@PathVariable Long competitionId,
                                                  @Valid @RequestBody RescoreRequest request) {
        return ResponseEntity.ok(rescoreService.initiateRescore(competitionId, request));
    }

    @PostMapping("/rescore/{roundId}/scores")
    public ResponseEntity<Void> submitScores(@PathVariable Long competitionId,
                                              @PathVariable Long roundId,
                                              @CurrentUser JwtUserDetails userDetails,
                                              @Valid @RequestBody ScoreSubmitRequest request) {
        rescoreService.submitRescoreScores(competitionId, roundId, userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rescore-rounds")
    public ResponseEntity<List<RescoreRound>> listRounds(@PathVariable Long competitionId) {
        return ResponseEntity.ok(rescoreService.getRounds(competitionId));
    }
}
```

---

### Task 13: 报表控制器

**Files:**
- Create: `backend/src/main/java/com/multimediareview/controller/ReportController.java`

- [ ] **Step 1: 创建 ReportController**

`backend/src/main/java/com/multimediareview/controller/ReportController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.dto.ReportResponse;
import com.multimediareview.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
@RequiredArgsConstructor
public class ReportController {

    private final RankingService rankingService;

    @GetMapping("/report")
    public ResponseEntity<ReportResponse> report(@PathVariable Long competitionId) {
        return ResponseEntity.ok(rankingService.generateReport(competitionId));
    }

    @GetMapping("/ties")
    public ResponseEntity<List<List<ReportResponse.ParticipantScoreDetail>>> findTies(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(rankingService.findTies(competitionId));
    }
}
```

---

## Phase 5: 全局异常处理

### Task 14: 全局异常处理器

**Files:**
- Create: `backend/src/main/java/com/multimediareview/dto/ApiError.java`
- Create: `backend/src/main/java/com/multimediareview/config/GlobalExceptionHandler.java`

- [ ] **Step 1: 创建 ApiError**

`backend/src/main/java/com/multimediareview/dto/ApiError.java`:
```java
package com.multimediareview.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

- [ ] **Step 2: 创建 GlobalExceptionHandler**

`backend/src/main/java/com/multimediareview/config/GlobalExceptionHandler.java`:
```java
package com.multimediareview.config;

import com.multimediareview.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException e) {
        return ResponseEntity.badRequest().body(ApiError.builder()
                .status(400)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return ResponseEntity.badRequest().body(ApiError.builder()
                .status(400)
                .message(msg)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
```

---

## Phase 6: 前端项目

### Task 15: 创建 React 前端项目骨架

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
- Create: `frontend/index.html`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/App.tsx`

- [ ] **Step 1: 初始化项目**

Run:
```bash
cd frontend
npm create vite@latest . -- --template react-ts
npm install antd @ant-design/icons axios react-router-dom dayjs
npm install -D @types/node
```

- [ ] **Step 2: 配置 vite.config.ts 代理**

`frontend/vite.config.ts`:
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 API 客户端**

`frontend/src/api/client.ts`:
```typescript
import axios from 'axios';

const client = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default client;
```

- [ ] **Step 4: 创建路由和 App 入口**

`frontend/src/App.tsx`:
```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import CompetitionSetupPage from './pages/CompetitionSetupPage';
import ScoringPage from './pages/ScoringPage';
import ReportPage from './pages/ReportPage';

function PrivateRoute({ children, role }: { children: React.ReactNode; role?: string }) {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');
  if (!token) return <Navigate to="/login" />;
  if (role && userRole !== role) return <Navigate to="/" />;
  return <>{children}</>;
}

export default function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="/competition/:id/setup" element={<PrivateRoute role="ADMIN"><CompetitionSetupPage /></PrivateRoute>} />
          <Route path="/competition/:id/score" element={<PrivateRoute role="JUDGE"><ScoringPage /></PrivateRoute>} />
          <Route path="/competition/:id/report" element={<PrivateRoute><ReportPage /></PrivateRoute>} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}
```

`frontend/src/main.tsx`:
```typescript
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

---

### Task 16: 登录页面

**Files:**
- Create: `frontend/src/pages/LoginPage.tsx`

- [ ] **Step 1: 创建 LoginPage**

`frontend/src/pages/LoginPage.tsx`:
```typescript
import { useState } from 'react';
import { Form, Input, Button, Card, message, Tabs } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: any, isRegister: boolean) => {
    setLoading(true);
    try {
      const url = isRegister ? '/auth/register' : '/auth/login';
      if (isRegister) {
        values.role = values.role || 'JUDGE';
      }
      const { data } = await client.post(url, values);
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('username', data.username);
      localStorage.setItem('name', data.name);
      localStorage.setItem('role', data.role);
      message.success(isRegister ? '注册成功' : '登录成功');
      navigate('/');
    } catch (err: any) {
      message.error(err.response?.data?.message || '操作失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: '#f0f2f5' }}>
      <Card title="多媒体作品评审系统" style={{ width: 400 }}>
        <Tabs items={[
          {
            key: 'login',
            label: '登录',
            children: (
              <Form onFinish={(v) => onFinish(v, false)}>
                <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                  <Input prefix={<UserOutlined />} placeholder="用户名" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>登录</Button>
                </Form.Item>
              </Form>
            )
          },
          {
            key: 'register',
            label: '注册',
            children: (
              <Form onFinish={(v) => onFinish({ ...v, role: 'JUDGE' }, true)}>
                <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                  <Input prefix={<UserOutlined />} placeholder="用户名" />
                </Form.Item>
                <Form.Item name="name" rules={[{ required: true, message: '请输入姓名' }]}>
                  <Input placeholder="姓名" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, min: 6, message: '至少6位密码' }]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="密码" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading} block>注册</Button>
                </Form.Item>
              </Form>
            )
          }
        ]} />
      </Card>
    </div>
  );
}
```

---

### Task 17: 管理员仪表盘

**Files:**
- Create: `frontend/src/pages/DashboardPage.tsx`

- [ ] **Step 1: 创建 DashboardPage**

`frontend/src/pages/DashboardPage.tsx`:
```typescript
import { useEffect, useState } from 'react';
import { Button, Card, Table, Space, Tag, Modal, Form, Input, InputNumber, message, Descriptions } from 'antd';
import { PlusOutlined, PlayCircleOutlined, StopOutlined, EyeOutlined, EditOutlined, LogoutOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';

export default function DashboardPage() {
  const [competitions, setCompetitions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [createVisible, setCreateVisible] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const role = localStorage.getItem('role');

  const fetchList = async () => {
    setLoading(true);
    try {
      const { data } = await client.get('/competitions');
      setCompetitions(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchList(); }, []);

  const handleCreate = async (values: any) => {
    try {
      await client.post('/competitions', {
        name: values.name,
        description: values.description,
        maxRank: values.maxRank,
        scoreModifyWindowMinutes: values.scoreModifyWindowMinutes || 10,
        rankConfigs: Array.from({ length: values.maxRank }, (_, i) => ({
          rankNumber: i + 1,
          capacity: values[`capacity_${i + 1}`] || 1
        }))
      });
      message.success('比赛创建成功');
      setCreateVisible(false);
      form.resetFields();
      fetchList();
    } catch (err: any) {
      message.error(err.response?.data?.message || '创建失败');
    }
  };

  const handleStart = async (id: number) => {
    await client.put(`/competitions/${id}/start`);
    message.success('比赛已开始');
    fetchList();
  };

  const handleFinish = async (id: number) => {
    Modal.confirm({
      title: '确认结束比赛?',
      content: '结束后将生成最终排名，不可再修改评分。',
      onOk: async () => {
        await client.put(`/competitions/${id}/finish`);
        message.success('比赛已结束');
        fetchList();
      }
    });
  };

  const logout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const columns = [
    { title: '比赛名称', dataIndex: 'name', key: 'name' },
    { title: '状态', dataIndex: 'status', key: 'status', render: (s: string) => {
      const map: Record<string, { color: string; text: string }> = {
        DRAFT: { color: 'default', text: '草稿' },
        SCORING: { color: 'processing', text: '评分中' },
        FINISHED: { color: 'success', text: '已结束' }
      };
      return <Tag color={map[s]?.color}>{map[s]?.text || s}</Tag>;
    }},
    { title: '名次数', dataIndex: 'maxRank', key: 'maxRank' },
    { title: '参评人数', dataIndex: 'participantCount', key: 'participantCount' },
    { title: '评委数', dataIndex: 'judgeCount', key: 'judgeCount' },
    { title: '创建者', dataIndex: 'createdByName', key: 'createdByName' },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Space>
          {record.status === 'DRAFT' && role === 'ADMIN' && (
            <>
              <Button size="small" icon={<EditOutlined />}
                onClick={() => navigate(`/competition/${record.id}/setup`)}>配置</Button>
              <Button size="small" type="primary" icon={<PlayCircleOutlined />}
                onClick={() => handleStart(record.id)}>开始</Button>
            </>
          )}
          {record.status === 'SCORING' && role === 'ADMIN' && (
            <Button size="small" danger icon={<StopOutlined />}
              onClick={() => handleFinish(record.id)}>结束</Button>
          )}
          {record.status === 'SCORING' && role === 'JUDGE' && (
            <Button size="small" type="primary"
              onClick={() => navigate(`/competition/${record.id}/score`)}>评分</Button>
          )}
          {record.status === 'FINISHED' && (
            <Button size="small" icon={<EyeOutlined />}
              onClick={() => navigate(`/competition/${record.id}/report`)}>查看报表</Button>
          )}
          {record.status === 'DRAFT' && role === 'JUDGE' && <span>-</span>}
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h1>多媒体作品评审系统</h1>
        <Space>
          <span>{localStorage.getItem('name')} ({role === 'ADMIN' ? '管理员' : '评委'})</span>
          {role === 'ADMIN' && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateVisible(true)}>创建比赛</Button>
          )}
          <Button icon={<LogoutOutlined />} onClick={logout}>退出</Button>
        </Space>
      </div>

      <Table columns={columns} dataSource={competitions} rowKey="id" loading={loading} />

      <Modal title="创建比赛" open={createVisible} onCancel={() => setCreateVisible(false)}
        onOk={() => form.submit()} width={600}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="name" label="比赛名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="maxRank" label="名次数量" rules={[{ required: true }]}
            extra="最多10名">
            <InputNumber min={1} max={10} />
          </Form.Item>
          <Form.Item name="scoreModifyWindowMinutes" label="评分修改时限(分钟)" initialValue={10}>
            <InputNumber min={1} max={60} />
          </Form.Item>
          <Form.Item noStyle shouldUpdate={(prev, cur) => prev.maxRank !== cur.maxRank}>
            {({ getFieldValue }) => {
              const maxRank = getFieldValue('maxRank') || 0;
              return Array.from({ length: maxRank }, (_, i) => (
                <Form.Item key={i} name={`capacity_${i + 1}`}
                  label={`第${i + 1}名名额`} rules={[{ required: true }]}
                  initialValue={1}>
                  <InputNumber min={1} />
                </Form.Item>
              ));
            }}
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
```

---

### Task 18: 比赛配置页（参评人 + 文件上传 + 评委分配）

**Files:**
- Create: `frontend/src/pages/CompetitionSetupPage.tsx`

- [ ] **Step 1: 创建 CompetitionSetupPage**

`frontend/src/pages/CompetitionSetupPage.tsx`:
```typescript
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tabs, Table, Button, Modal, Form, Input, Upload, message, Space, Popconfirm, Select } from 'antd';
import { PlusOutlined, UploadOutlined, DeleteOutlined, ArrowLeftOutlined, UserAddOutlined } from '@ant-design/icons';
import type { UploadFile } from 'antd/es/upload/interface';
import client from '../api/client';

export default function CompetitionSetupPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [participants, setParticipants] = useState<any[]>([]);
  const [judges, setJudges] = useState<any[]>([]);
  const [allJudges, setAllJudges] = useState<any[]>([]);
  const [addVisible, setAddVisible] = useState(false);
  const [addJudgeVisible, setAddJudgeVisible] = useState(false);
  const [form] = Form.useForm();
  const [judgeForm] = Form.useForm();

  const fetchParticipants = async () => {
    const { data } = await client.get(`/competitions/${id}/participants`);
    setParticipants(data);
  };

  const fetchJudges = async () => {
    const { data } = await client.get(`/competitions/${id}/judges`);
    setJudges(data);
  };

  const fetchAllJudges = async () => {
    // 简化处理：从已注册用户中筛选评委角色
    try {
      const { data } = await client.get('/users?role=JUDGE');
      setAllJudges(data);
    } catch {
      setAllJudges([]);
    }
  };

  useEffect(() => {
    fetchParticipants();
    fetchJudges();
    fetchAllJudges();
  }, [id]);

  const handleAddParticipant = async (values: any) => {
    await client.post(`/competitions/${id}/participants`, values);
    message.success('参评人添加成功');
    setAddVisible(false);
    form.resetFields();
    fetchParticipants();
  };

  const handleDeleteParticipant = async (pid: number) => {
    await client.delete(`/competitions/${id}/participants/${pid}`);
    message.success('删除成功');
    fetchParticipants();
  };

  const handleUploadFile = async (pid: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    await client.post(`/competitions/${id}/participants/${pid}/files`, formData);
    message.success('文件上传成功');
    fetchParticipants();
  };

  const handleAssignJudges = async (values: any) => {
    await client.post(`/competitions/${id}/judges`, { userIds: values.userIds });
    message.success('评委分配成功');
    setAddJudgeVisible(false);
    judgeForm.resetFields();
    fetchJudges();
  };

  const handleRemoveJudge = async (jid: number) => {
    await client.delete(`/competitions/${id}/judges/${jid}`);
    message.success('评委移除成功');
    fetchJudges();
  };

  const participantColumns = [
    { title: '姓名', dataIndex: 'name', key: 'name' },
    { title: '部门/描述', dataIndex: 'department', key: 'department' },
    {
      title: '稿件文件', key: 'files',
      render: (_: any, record: any) => (
        <Space direction="vertical" size="small">
          {record.files?.map((f: any) => (
            <a key={f.id} href={f.downloadUrl} target="_blank" rel="noopener">
              [{f.fileType}] {f.originalName} ({(f.fileSize / 1024 / 1024).toFixed(1)}MB)
            </a>
          ))}
          <Upload showUploadList={false} accept=".txt,.doc,.docx,.pdf,.mp4,.mov,.avi,.mkv,.mp3,.wav,.aac,.flac"
            beforeUpload={(file) => { handleUploadFile(record.id, file); return false; }}>
            <Button size="small" icon={<UploadOutlined />}>上传文件</Button>
          </Upload>
        </Space>
      )
    },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Popconfirm title="确认删除?" onConfirm={() => handleDeleteParticipant(record.id)}>
          <Button size="small" danger icon={<DeleteOutlined />} />
        </Popconfirm>
      )
    }
  ];

  const judgeColumns = [
    { title: '姓名', dataIndex: 'name', key: 'name' },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    {
      title: '操作', key: 'actions',
      render: (_: any, record: any) => (
        <Popconfirm title="确认移除?" onConfirm={() => handleRemoveJudge(record.id)}>
          <Button size="small" danger>移除</Button>
        </Popconfirm>
      )
    }
  ];

  return (
    <div style={{ padding: 24, maxWidth: 1000, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>比赛配置</h2>
      </Space>

      <Tabs items={[
        {
          key: 'participants',
          label: `参评人 (${participants.length})`,
          children: (
            <>
              <Button type="primary" icon={<PlusOutlined />} style={{ marginBottom: 16 }}
                onClick={() => setAddVisible(true)}>添加参评人</Button>
              <Table columns={participantColumns} dataSource={participants} rowKey="id" />
            </>
          )
        },
        {
          key: 'judges',
          label: `评委 (${judges.length})`,
          children: (
            <>
              <Button type="primary" icon={<UserAddOutlined />} style={{ marginBottom: 16 }}
                onClick={() => setAddJudgeVisible(true)}>分配评委</Button>
              <Table columns={judgeColumns} dataSource={judges} rowKey="id" />
            </>
          )
        }
      ]} />

      <Modal title="添加参评人" open={addVisible} onCancel={() => setAddVisible(false)}
        onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleAddParticipant}>
          <Form.Item name="name" label="姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="department" label="部门/描述">
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="分配评委" open={addJudgeVisible} onCancel={() => setAddJudgeVisible(false)}
        onOk={() => judgeForm.submit()}>
        <Form form={judgeForm} layout="vertical" onFinish={handleAssignJudges}>
          <Form.Item name="userIds" label="选择评委" rules={[{ required: true }]}>
            <Select mode="multiple" placeholder="选择评委用户" options={
              allJudges.map((j: any) => ({ label: `${j.name} (${j.username})`, value: j.id }))
            } />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
```

同时后端需要增加获取所有评委用户的接口。在 `CompetitionController` 或新建 `UserController`:

`backend/src/main/java/com/multimediareview/controller/UserController.java`:
```java
package com.multimediareview.controller;

import com.multimediareview.entity.enums.UserRole;
import com.multimediareview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers(@RequestParam(required = false) String role) {
        var users = role != null
                ? userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(role))
                    .collect(Collectors.toList())
                : userRepository.findAll();
        return ResponseEntity.ok(users.stream().map(u -> Map.of(
                "id", (Object) u.getId(),
                "username", u.getUsername(),
                "name", u.getName(),
                "role", u.getRole().name()
        )).collect(Collectors.toList()));
    }
}
```

---

### Task 19: 评委评分页

**Files:**
- Create: `frontend/src/pages/ScoringPage.tsx`

- [ ] **Step 1: 创建 ScoringPage**

`frontend/src/pages/ScoringPage.tsx`:
```typescript
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Form, InputNumber, Button, message, Space, Descriptions, Tag, Modal } from 'antd';
import { ArrowLeftOutlined, SendOutlined, EyeOutlined } from '@ant-design/icons';
import client from '../api/client';

export default function ScoringPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [competition, setCompetition] = useState<any>(null);
  const [participants, setParticipants] = useState<any[]>([]);
  const [myScores, setMyScores] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [previewFile, setPreviewFile] = useState<string | null>(null);

  const fetchData = async () => {
    const [compRes, partRes, scoreRes] = await Promise.all([
      client.get(`/competitions/${id}`),
      client.get(`/competitions/${id}/participants`),
      client.get(`/competitions/${id}/my-scores`)
    ]);
    setCompetition(compRes.data);
    setParticipants(partRes.data);
    setMyScores(scoreRes.data);
  };

  useEffect(() => { fetchData(); }, [id]);

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const scores = Object.entries(values).map(([key, score]) => ({
        participantId: Number(key.replace('score_', '')),
        score
      }));
      await client.post(`/competitions/${id}/scores`, { scores });
      message.success('评分提交成功');
      fetchData();
    } catch (err: any) {
      message.error(err.response?.data?.message || '提交失败');
    } finally {
      setLoading(false);
    }
  };

  const getMyScore = (participantId: number) => {
    return myScores.find((s: any) => s.participantId === participantId);
  };

  if (!competition) return null;

  const statusTag = getStatusTag(competition.status);

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>{competition.name} - 评分</h2>
        {statusTag}
      </Space>

      <Form onFinish={handleSubmit} layout="vertical">
        {participants.map((p: any) => {
          const existing = getMyScore(p.id);
          return (
            <Card key={p.id} style={{ marginBottom: 12 }}
              title={
                <Space>
                  <span>{p.name}</span>
                  {p.department && <Tag>{p.department}</Tag>}
                  {existing && <Tag color={existing.status === 'LOCKED' ? 'red' : 'blue'}>
                    {existing.status === 'LOCKED' ? '已锁定' : '已提交(可修改)'}
                  </Tag>}
                </Space>
              }
              extra={
                <Space>
                  {p.files?.map((f: any) => (
                    <Button key={f.id} size="small" icon={<EyeOutlined />}
                      onClick={() => setPreviewFile(f.downloadUrl)}>
                      [{f.fileType}] {f.originalName}
                    </Button>
                  ))}
                </Space>
              }>
              <Form.Item name={`score_${p.id}`}
                rules={[{ required: true, message: '请打分' }]}
                initialValue={existing?.score}>
                <InputNumber min={0} max={100} step={0.1}
                  style={{ width: 200 }}
                  disabled={existing?.status === 'LOCKED'}
                  addonAfter="分" />
              </Form.Item>
            </Card>
          );
        })}
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}
            icon={<SendOutlined />} size="large" block
            disabled={competition.status !== 'SCORING'}>
            提交评分
          </Button>
        </Form.Item>
      </Form>

      <Modal title="稿件预览" open={!!previewFile} onCancel={() => setPreviewFile(null)}
        footer={null} width={800}>
        {previewFile && (
          previewFile.match(/\.(mp4|mov|avi|mkv)$/i)
            ? <video controls style={{ width: '100%' }} src={previewFile} />
            : previewFile.match(/\.(mp3|wav|aac|flac)$/i)
              ? <audio controls style={{ width: '100%' }} src={previewFile} />
              : <iframe src={previewFile} style={{ width: '100%', height: 500 }} />
        )}
      </Modal>
    </div>
  );
}

function getStatusTag(status: string) {
  const map: Record<string, { color: string; text: string }> = {
    DRAFT: { color: 'default', text: '草稿' },
    SCORING: { color: 'processing', text: '评分中' },
    FINISHED: { color: 'success', text: '已结束' }
  };
  return <Tag color={map[status]?.color}>{map[status]?.text || status}</Tag>;
}
```

---

### Task 20: 排名报表页

**Files:**
- Create: `frontend/src/pages/ReportPage.tsx`

- [ ] **Step 1: 创建 ReportPage**

`frontend/src/pages/ReportPage.tsx`:
```typescript
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Collapse, Table, Tag, Button, Space, Spin } from 'antd';
import { ArrowLeftOutlined, TrophyOutlined } from '@ant-design/icons';
import client from '../api/client';

const rankColors = ['#ffd700', '#c0c0c0', '#cd7f32', '#1890ff', '#52c41a',
  '#722ed1', '#eb2f96', '#fa8c16', '#13c2c2', '#2f54eb'];

export default function ReportPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [report, setReport] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const { data } = await client.get(`/competitions/${id}/report`);
        setReport(data);
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  if (loading) return <Spin size="large" style={{ display: 'block', margin: '200px auto' }} />;
  if (!report) return null;

  return (
    <div style={{ padding: 24, maxWidth: 1000, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/')}>返回</Button>
        <h2 style={{ margin: 0 }}>{report.competitionName} - 排名报表</h2>
        <Tag color="success">已结束</Tag>
      </Space>

      {report.status !== 'FINISHED' && (
        <Card style={{ marginBottom: 16, background: '#fff7e6' }}>
          比赛尚未结束，当前排名为预览结果，最终排名将在比赛结束后确定。
        </Card>
      )}

      {report.ranks?.map((rank: any) => (
        <Card key={rank.rankNumber} style={{ marginBottom: 16 }}
          title={
            <Space>
              <TrophyOutlined style={{ color: rankColors[rank.rankNumber - 1] || '#666' }} />
              <span style={{ fontSize: 18, fontWeight: 'bold', color: rankColors[rank.rankNumber - 1] }}>
                {rank.rankLabel}
              </span>
              <Tag>{rank.participants.length}人</Tag>
            </Space>
          }>
          {rank.participants.map((p: any) => (
            <Collapse key={p.participantId} style={{ marginBottom: 8 }}
              items={[{
                key: 'detail',
                label: (
                  <Space>
                    <strong>{p.participantName}</strong>
                    {p.department && <Tag>{p.department}</Tag>}
                    <Tag color="blue">平均分: {p.averageScore}</Tag>
                  </Space>
                ),
                children: (
                  <div>
                    <p><strong>计算过程：</strong>{p.calculationProcess}</p>
                    {p.removedHighest && (
                      <p>去掉最高分: <Tag color="red">{p.removedHighest}</Tag> | 去掉最低分: <Tag color="blue">{p.removedLowest}</Tag></p>
                    )}
                    <p>有效分: {p.effectiveScores?.join(', ')}</p>
                    <Table size="small" pagination={false} dataSource={p.judgeScores}
                      columns={[
                        { title: '评委', dataIndex: 'judgeName', key: 'judgeName' },
                        { title: '评分', dataIndex: 'score', key: 'score' },
                        {
                          title: '标记', key: 'mark',
                          render: (_: any, record: any) => (
                            <Space>
                              {record.isHighest && <Tag color="red">最高分</Tag>}
                              {record.isLowest && <Tag color="blue">最低分</Tag>}
                            </Space>
                          )
                        }
                      ]}
                      rowKey="judgeId" />
                  </div>
                )
              }]} />
          ))}
          {rank.participants.length === 0 && <p style={{ color: '#999' }}>暂无人获得此名次</p>}
        </Card>
      ))}
    </div>
  );
}
```

---

## Phase 7: 集成测试与启动

### Task 21: 后端修复与验证

- [ ] **Step 1: 修复 ScoreRepository 查询方法**

`ScoreRepository` 中 `getAllScores` 使用的 `findByCompetitionIdAndJudgeId` 无法查所有评委的分数。添加专用查询:

```java
@Query("SELECT s FROM Score s JOIN FETCH s.judge JOIN FETCH s.participant WHERE s.competition.id = :competitionId")
List<Score> findAllByCompetitionId(@Param("competitionId") Long competitionId);
```

并更新 ScoreService.getAllScores:
```java
public List<ScoreResponse> getAllScores(Long competitionId) {
    return scoreRepository.findAllByCompetitionId(competitionId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}
```

- [ ] **Step 2: 验证后端启动和 API**

```bash
cd backend && mvn spring-boot:run
```

用 curl 测试:
```bash
# 注册管理员
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","name":"管理员","role":"ADMIN"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

- [ ] **Step 3: 启动前端**

```bash
cd frontend && npm install && npm run dev
```

访问 http://localhost:5173 验证功能。

---

## 验证清单

- [ ] 管理员注册/登录
- [ ] 创建比赛（配置名次和名额）
- [ ] 添加参评人 + 上传稿件（文本/视频/音频）
- [ ] 分配评委
- [ ] 开始比赛
- [ ] 评委评分（盲评，互不可见）
- [ ] 评分提交 + 10分钟内修改
- [ ] 超时自动锁定
- [ ] 结束比赛
- [ ] 查看排名报表（含详细计算过程、最高最低分标记）
- [ ] 同分检测与复评流程
