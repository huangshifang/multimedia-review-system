package com.multimediareview.controller;

import com.multimediareview.dto.ParticipantRequest;
import com.multimediareview.dto.ParticipantResponse;
import com.multimediareview.entity.ParticipantFile;
import com.multimediareview.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

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
        ParticipantFile pf = participantService.getFileInfo(fid);
        Resource resource = participantService.loadFileResource(fid);
        MediaType mediaType = getMediaType(pf.getOriginalName());
        String disposition = isInlineType(pf.getOriginalName())
                ? "inline" : "attachment";
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        disposition + "; filename=\"" + pf.getOriginalName() + "\"")
                .body(resource);
    }

    private MediaType getMediaType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.valueOf("image/webp");
        if (lower.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        if (lower.endsWith(".bmp")) return MediaType.valueOf("image/bmp");
        if (lower.endsWith(".mp4")) return MediaType.valueOf("video/mp4");
        if (lower.endsWith(".mov")) return MediaType.valueOf("video/quicktime");
        if (lower.endsWith(".avi")) return MediaType.valueOf("video/x-msvideo");
        if (lower.endsWith(".mkv")) return MediaType.valueOf("video/x-matroska");
        if (lower.endsWith(".wmv")) return MediaType.valueOf("video/x-ms-wmv");
        if (lower.endsWith(".webm")) return MediaType.valueOf("video/webm");
        if (lower.endsWith(".mp3")) return MediaType.valueOf("audio/mpeg");
        if (lower.endsWith(".wav")) return MediaType.valueOf("audio/wav");
        if (lower.endsWith(".aac")) return MediaType.valueOf("audio/aac");
        if (lower.endsWith(".flac")) return MediaType.valueOf("audio/flac");
        if (lower.endsWith(".ogg")) return MediaType.valueOf("audio/ogg");
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".txt")) return MediaType.TEXT_PLAIN;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private boolean isInlineType(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif")
                || lower.endsWith(".webp") || lower.endsWith(".svg")
                || lower.endsWith(".bmp") || lower.endsWith(".pdf")
                || lower.endsWith(".txt") || lower.endsWith(".mp4")
                || lower.endsWith(".mov") || lower.endsWith(".mp3")
                || lower.endsWith(".wav") || lower.endsWith(".aac")
                || lower.endsWith(".flac");
    }
}
