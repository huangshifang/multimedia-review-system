package com.multimediareview.service;

import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.dto.ParticipantRequest;
import com.multimediareview.dto.ParticipantResponse;
import com.multimediareview.entity.*;
import com.multimediareview.repository.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipantService {

    private final CompetitionParticipantRepository participantRepository;
    private final ParticipantFileRepository fileRepository;
    private final CompetitionRepository competitionRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final CompetitionService competitionService;

    public ParticipantService(CompetitionParticipantRepository participantRepository,
                              ParticipantFileRepository fileRepository,
                              CompetitionRepository competitionRepository,
                              FileStorageService fileStorageService,
                              UserRepository userRepository,
                              CompetitionService competitionService) {
        this.participantRepository = participantRepository;
        this.fileRepository = fileRepository;
        this.competitionRepository = competitionRepository;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.competitionService = competitionService;
    }

    @Transactional
    public ParticipantResponse addParticipant(Long competitionId, ParticipantRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        CompetitionParticipant participant = CompetitionParticipant.builder()
                .competition(competition)
                .name(request.getName())
                .department(request.getDepartment())
                .userId(request.getUserId())
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
        fileRepository.save(pf);

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

    public ParticipantFile getFileInfo(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
    }

    public Resource loadFileResource(Long fileId) {
        ParticipantFile pf = getFileInfo(fileId);
        return fileStorageService.loadFile(pf);
    }

    // --- Self-service methods for PARTICIPANT role ---

    public List<CompetitionResponse> getMyCompetitions(Long userId) {
        List<CompetitionParticipant> entries = participantRepository.findByUserId(userId);
        return entries.stream()
                .map(CompetitionParticipant::getCompetition)
                .distinct()
                .map(c -> competitionService.toResponse(c))
                .collect(Collectors.toList());
    }

    public List<ParticipantResponse> getMyEntries(Long competitionId, Long userId) {
        return participantRepository.findByCompetitionIdAndUserId(competitionId, userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipantResponse uploadMyFile(Long competitionId, Long userId, MultipartFile file) {
        List<CompetitionParticipant> entries = participantRepository
                .findByCompetitionIdAndUserId(competitionId, userId);
        if (entries.isEmpty()) {
            throw new RuntimeException("你在此比赛中没有参评条目");
        }
        return uploadFile(competitionId, entries.get(0).getId(), file);
    }

    @Transactional
    public void deleteMyFile(Long fileId, Long userId) {
        ParticipantFile pf = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        CompetitionParticipant participant = pf.getParticipant();
        if (participant == null || !userId.equals(participant.getUserId())) {
            throw new RuntimeException("无权操作此文件");
        }
        fileStorageService.deleteFile(pf);
        fileRepository.delete(pf);
    }

    // --- Response mapping ---

    private ParticipantResponse toResponse(CompetitionParticipant p) {
        List<ParticipantFile> files = fileRepository.findByParticipantId(p.getId());
        String linkedUsername = null;
        if (p.getUserId() != null) {
            linkedUsername = userRepository.findById(p.getUserId())
                    .map(User::getUsername)
                    .orElse(null);
        }
        return ParticipantResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .department(p.getDepartment())
                .userId(p.getUserId())
                .linkedUsername(linkedUsername)
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
