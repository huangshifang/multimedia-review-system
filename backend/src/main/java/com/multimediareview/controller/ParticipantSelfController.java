package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.dto.ParticipantResponse;
import com.multimediareview.entity.ParticipantFile;
import com.multimediareview.service.ParticipantService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
public class ParticipantSelfController {

    private final ParticipantService participantService;

    public ParticipantSelfController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("/competitions")
    public ResponseEntity<List<CompetitionResponse>> getMyCompetitions(@CurrentUser JwtUserDetails user) {
        return ResponseEntity.ok(participantService.getMyCompetitions(user.getUserId()));
    }

    @GetMapping("/competitions/{id}/entries")
    public ResponseEntity<List<ParticipantResponse>> getMyEntries(@PathVariable Long id,
                                                                   @CurrentUser JwtUserDetails user) {
        return ResponseEntity.ok(participantService.getMyEntries(id, user.getUserId()));
    }

    @PostMapping("/competitions/{id}/files")
    public ResponseEntity<ParticipantResponse> uploadFile(@PathVariable Long id,
                                                           @RequestParam("file") MultipartFile file,
                                                           @CurrentUser JwtUserDetails user) {
        return ResponseEntity.ok(participantService.uploadMyFile(id, user.getUserId(), file));
    }

    @DeleteMapping("/files/{fid}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fid,
                                           @CurrentUser JwtUserDetails user) {
        participantService.deleteMyFile(fid, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/files/{fid}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fid) {
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
        if (lower.endsWith(".mp4")) return MediaType.valueOf("video/mp4");
        if (lower.endsWith(".mov")) return MediaType.valueOf("video/quicktime");
        if (lower.endsWith(".avi")) return MediaType.valueOf("video/x-msvideo");
        if (lower.endsWith(".mkv")) return MediaType.valueOf("video/x-matroska");
        if (lower.endsWith(".mp3")) return MediaType.valueOf("audio/mpeg");
        if (lower.endsWith(".wav")) return MediaType.valueOf("audio/wav");
        if (lower.endsWith(".aac")) return MediaType.valueOf("audio/aac");
        if (lower.endsWith(".flac")) return MediaType.valueOf("audio/flac");
        if (lower.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".txt")) return MediaType.TEXT_PLAIN;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private boolean isInlineType(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".txt")
                || lower.endsWith(".mp4") || lower.endsWith(".mov")
                || lower.endsWith(".mp3") || lower.endsWith(".wav")
                || lower.endsWith(".aac") || lower.endsWith(".flac");
    }
}
