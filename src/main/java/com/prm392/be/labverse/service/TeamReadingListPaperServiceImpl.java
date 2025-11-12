package com.prm392.be.labverse.service;

import com.prm392.be.labverse.constant.EPriority;
import com.prm392.be.labverse.dto.team.TeamReadingListPaperResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.Team;
import com.prm392.be.labverse.entity.TeamReadingList;
import com.prm392.be.labverse.entity.TeamReadingListPaper;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.PaperErrorCode;
import com.prm392.be.labverse.exception.TeamErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.MembershipRepository;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.TeamReadingListPaperRepository;
import com.prm392.be.labverse.repository.TeamReadingListRepository;
import com.prm392.be.labverse.repository.TeamRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamReadingListPaperServiceImpl implements TeamReadingListPaperService {

    private final TeamReadingListPaperRepository teamReadingListPaperRepository;
    private final TeamReadingListRepository teamReadingListRepository;
    private final PaperRepository paperRepository;
    private final TeamRepository teamRepository;
    private final MembershipRepository membershipRepository;


    private void verifyMembershipOrOwner(String teamId, String currentUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));
        boolean isOwner = team.getCreatedBy() != null && team.getCreatedBy().getId().equals(currentUserId);
        boolean isMember = membershipRepository.existsByTeam_IdAndUserId_Id(teamId, currentUserId);
        if (!isOwner && !isMember) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }
    }

    private TeamReadingList verifyReadingList(String teamId, String readingListId) {
        TeamReadingList readingList = teamReadingListRepository.findById(readingListId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_FOUND));
        if (!readingList.getTeam().getId().equals(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_IN_TEAM);
        }
        if (readingList.isDeleteFlag()) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_DELETED);
        }
        return readingList;
    }

    @Override
    @Transactional
    public TeamReadingListPaperResponse setPaperPriority(String teamId, String readingListId, String paperId, EPriority priority) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        // Verify team exists and current user is owner or member
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_NOT_FOUND));
        boolean isOwner = team.getCreatedBy() != null && team.getCreatedBy().getId().equals(currentUserId);
        boolean isMember = membershipRepository.existsByTeam_IdAndUserId_Id(teamId, currentUserId);
        if (!isOwner && !isMember) {
            throw new AppException(TeamErrorCode.TEAM_NO_PERMISSION);
        }

        // Verify reading list
        TeamReadingList readingList = teamReadingListRepository.findById(readingListId)
                .orElseThrow(() -> new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_FOUND));
        if (!readingList.getTeam().getId().equals(teamId)) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_NOT_IN_TEAM);
        }
        if (readingList.isDeleteFlag()) {
            throw new AppException(TeamErrorCode.TEAM_READING_LIST_DELETED);
        }

        // Verify paper
        Paper paper = paperRepository.findByIdAndDeleteFlagFalse(paperId)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        // Upsert TeamReadingListPaper
        TeamReadingListPaper link = teamReadingListPaperRepository
                .findByTeamReadingList_IdAndPaper_Id(readingListId, paperId)
                .orElseGet(() -> TeamReadingListPaper.builder()
                        .teamReadingList(readingList)
                        .paper(paper)
                        .build());
        link.setPriority(priority);
        teamReadingListPaperRepository.save(link);

        return TeamReadingListPaperResponse.builder()
                .id(link.getId())
                .readingListId(readingList.getId())
                .paperId(paper.getId())
                .priority(link.getPriority())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<TeamReadingListPaperResponse> listPapers(String teamId, String readingListId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }
        verifyMembershipOrOwner(teamId, currentUserId);
        TeamReadingList readingList = verifyReadingList(teamId, readingListId);

        return teamReadingListPaperRepository.findAllByTeamReadingList_Id(readingList.getId())
                .stream()
                .map(link -> TeamReadingListPaperResponse.builder()
                        .id(link.getId())
                        .readingListId(readingList.getId())
                        .paperId(link.getPaper().getId())
                        .priority(link.getPriority())
                        .title(link.getPaper().getTitle())
                        .authorName(link.getPaper().getAuthorName())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }



    @Override
    @Transactional
    public TeamReadingListPaperResponse addPaper(String teamId, String readingListId, String paperId, EPriority priority) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        // User phải là owner hoặc member của team
        verifyMembershipOrOwner(teamId, currentUserId);

        // Check reading list thuộc team và chưa bị xóa
        TeamReadingList readingList = verifyReadingList(teamId, readingListId);

        // Tìm paper
        Paper paper = paperRepository.findByIdAndDeleteFlagFalse(paperId)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        // CHẶN: chỉ cho phép add paper mà current user là owner
        if (paper.getUser() == null || !paper.getUser().getId().equals(currentUserId)) {
            throw new AppException(PaperErrorCode.NOT_PAPER_OWNER);
        }

        // CHẶN: paper đã tồn tại trong reading list rồi thì không cho add nữa
        if (teamReadingListPaperRepository
                .existsByTeamReadingList_IdAndPaper_Id(readingListId, paperId)) {
            // dùng error code riêng nếu bạn có, ví dụ:
            // throw new AppException(TeamErrorCode.TEAM_READING_LIST_PAPER_ALREADY_EXISTS);
            throw new AppException(PaperErrorCode.PAPER_ALREADY_IN_READING_LIST);
        }

        // Nếu priority null thì default MEDIUM
        EPriority effectivePriority = (priority != null) ? priority : EPriority.MEDIUM;

        // Tạo mới link
        TeamReadingListPaper link = TeamReadingListPaper.builder()
                .teamReadingList(readingList)
                .paper(paper)
                .priority(effectivePriority)
                .build();

        teamReadingListPaperRepository.save(link);

        return TeamReadingListPaperResponse.builder()
                .id(link.getId())
                .readingListId(readingList.getId())
                .paperId(paper.getId())
                .priority(link.getPriority())
                .title(paper.getTitle())
                .authorName(paper.getAuthorName())
                .build();
    }



    @Override
    @Transactional
    public void removePaper(String teamId, String readingListId, String paperId) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }
        verifyMembershipOrOwner(teamId, currentUserId);
        TeamReadingList readingList = verifyReadingList(teamId, readingListId);

        teamReadingListPaperRepository
                .findByTeamReadingList_IdAndPaper_Id(readingList.getId(), paperId)
                .ifPresent(teamReadingListPaperRepository::delete);
    }
}
