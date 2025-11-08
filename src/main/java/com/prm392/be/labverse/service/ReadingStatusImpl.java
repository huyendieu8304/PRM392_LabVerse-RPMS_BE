package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.readingStatus.ReadingStatusRequest;
import com.prm392.be.labverse.dto.readingStatus.ReadingStatusResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.ReadingStatus;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.PaperErrorCode;
import com.prm392.be.labverse.exception.ReadingStatusErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.ReadingStatusRepository;
import com.prm392.be.labverse.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReadingStatusImpl implements ReadingStatusService {

    ReadingStatusRepository readingStatusRepository;
    UserRepository userRepository;
    PaperRepository paperRepository;

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
}
