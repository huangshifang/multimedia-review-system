package com.multimediareview.service;

import com.multimediareview.dto.JudgeResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.UserRole;
import com.multimediareview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeService {

    private final CompetitionJudgeRepository judgeRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;

    public JudgeService(CompetitionJudgeRepository judgeRepository,
                        CompetitionRepository competitionRepository,
                        UserRepository userRepository) {
        this.judgeRepository = judgeRepository;
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
    }

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
