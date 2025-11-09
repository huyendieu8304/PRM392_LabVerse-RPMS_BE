package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.readingStatus.ReadingStatusRequest;
import com.prm392.be.labverse.dto.readingStatus.ReadingStatusResponse;
import com.prm392.be.labverse.dto.team.TeamReadingStatusResponse;
import com.prm392.be.labverse.entity.Membership;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.ReadingStatus;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.PaperErrorCode;
import com.prm392.be.labverse.exception.ReadingStatusErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReadingStatusImpl implements ReadingStatusService {

    ReadingStatusRepository readingStatusRepository;
    UserRepository userRepository;
    PaperRepository paperRepository;
    TeamRepository teamRepository;
    MembershipRepository membershipRepository;

    @Override
    public ReadingStatusResponse getReadingStatusInfo(String userId, String paperId) {
        ReadingStatus readingStatus = readingStatusRepository.findByUserIdAndPaperId(userId, paperId)
                .orElseThrow( () -> new AppException(ReadingStatusErrorCode.READING_STATUS_NOT_FOUND));

        return new ReadingStatusResponse(
                readingStatus.getId(),
                readingStatus.getUser().getId(),
                readingStatus.getPaper().getId(),
                readingStatus.getCurrentPage()
        );
    }

    @Override
    public ReadingStatusResponse createOrUpdate(ReadingStatusRequest request) {

        ReadingStatus readingStatus;

        if (request.id() != null) {
            // Update theo id nếu có
            readingStatus = readingStatusRepository.findById(request.id()).orElseThrow(
                    () -> new AppException(ReadingStatusErrorCode.READING_STATUS_NOT_FOUND)
            );
        } else {
            // Nếu id null thì tìm theo user + paper
            readingStatus = readingStatusRepository
                    .findByUserIdAndPaperId(request.userId(), request.paperId())
                    .orElse(null);

            if (readingStatus == null) {
                // chưa có -> tạo mới
                User user = userRepository.findById(request.userId()).orElseThrow(
                        () -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND)
                );
                Paper paper = paperRepository.findById(request.paperId()).orElseThrow(
                        () -> new AppException(PaperErrorCode.PAPER_NOT_FOUND)
                );
                readingStatus = new ReadingStatus();
                readingStatus.setUser(user);
                readingStatus.setPaper(paper);
            }
        }

        // cập nhật trang hiện tại
        readingStatus.setCurrentPage(request.currentPage());
        readingStatusRepository.save(readingStatus);

        return new ReadingStatusResponse(
                readingStatus.getId(),
                readingStatus.getUser().getId(),
                readingStatus.getPaper().getId(),
                readingStatus.getCurrentPage()
        );
    }

    @Override
    public List<TeamReadingStatusResponse> getTeamReadingStatus(String teamId, String paperId) {
        // Lấy danh sách các user trong team
        List<Membership> teamMembers = membershipRepository.findByTeam_IdOrderByName(teamId);
        List<TeamReadingStatusResponse> readingStatusList = new ArrayList<>();

        // Lấy tổng số trang của bài báo
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        // Lấy trạng thái đọc của mỗi user đối với bài báo
        for (Membership membership : teamMembers) {
            User user = membership.getUserId();  // Lấy thông tin user từ Membership
            ReadingStatus readingStatus = readingStatusRepository.findByUserIdAndPaperId(user.getId(), paperId)
                    .orElse(null);

            if (readingStatus != null) {
                readingStatusList.add(new TeamReadingStatusResponse(
                        readingStatus.getId(),
                        readingStatus.getUser().getId(),
                        readingStatus.getUser().getFullName(),
                        readingStatus.getUser().getEmail(),
                        readingStatus.getPaper().getId(),
                        readingStatus.getCurrentPage(),
                        paper.getTotalPage()  // Thêm tổng số trang từ Paper
                ));
            } else {
                // Nếu không tìm thấy reading status, có thể thêm trạng thái mặc định
                readingStatusList.add(new TeamReadingStatusResponse(
                        null,
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        paperId,
                        0,  // Trang bắt đầu mặc định là 0
                        paper.getTotalPage()  // Thêm tổng số trang từ Paper
                ));
            }
        }

        return readingStatusList;
    }


}
