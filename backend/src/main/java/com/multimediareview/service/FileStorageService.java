package com.multimediareview.service;

import com.multimediareview.entity.ParticipantFile;
import com.multimediareview.entity.enums.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private static final Set<String> EXECUTABLE_EXTENSIONS = Set.of(
        "exe", "bat", "cmd", "com", "scr", "pif", "msi", "msp", "mst",
        "vbs", "vbe", "js", "jse", "wsf", "wsh", "ps1", "psm1", "psd1",
        "sh", "bash", "csh", "ksh", "zsh", "fish",
        "py", "rb", "pl", "php", "jar", "class",
        "dll", "sys", "bin", "elf", "app", "apk"
    );

    private final Path uploadDir;
    private final Set<String> textExts, imageExts, videoExts, audioExts;
    private final long maxTextSize, maxImageSize, maxVideoSize, maxAudioSize;

    public FileStorageService(
            @Value("${app.file.upload-dir}") String uploadDir,
            @Value("${app.file.allowed-text-extensions}") String textExts,
            @Value("${app.file.allowed-image-extensions}") String imageExts,
            @Value("${app.file.allowed-video-extensions}") String videoExts,
            @Value("${app.file.allowed-audio-extensions}") String audioExts,
            @Value("${app.file.max-text-size}") long maxTextSize,
            @Value("${app.file.max-image-size}") long maxImageSize,
            @Value("${app.file.max-video-size}") long maxVideoSize,
            @Value("${app.file.max-audio-size}") long maxAudioSize) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.textExts = new HashSet<>(Arrays.asList(textExts.split(",")));
        this.imageExts = new HashSet<>(Arrays.asList(imageExts.split(",")));
        this.videoExts = new HashSet<>(Arrays.asList(videoExts.split(",")));
        this.audioExts = new HashSet<>(Arrays.asList(audioExts.split(",")));
        this.maxTextSize = maxTextSize;
        this.maxImageSize = maxImageSize;
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
        rejectExecutable(ext);
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

    private void rejectExecutable(String ext) {
        if (EXECUTABLE_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("禁止上传可执行文件类型: " + ext);
        }
    }

    private FileType detectFileType(String ext) {
        if (textExts.contains(ext)) return FileType.TEXT;
        if (imageExts.contains(ext)) return FileType.IMAGE;
        if (videoExts.contains(ext)) return FileType.VIDEO;
        if (audioExts.contains(ext)) return FileType.AUDIO;
        throw new RuntimeException("不支持的文件类型: " + ext);
    }

    private void validateSize(long size, FileType type) {
        long max = switch (type) {
            case TEXT -> maxTextSize;
            case IMAGE -> maxImageSize;
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
