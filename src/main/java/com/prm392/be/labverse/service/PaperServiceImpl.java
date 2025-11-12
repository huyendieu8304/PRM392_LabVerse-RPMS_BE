package com.prm392.be.labverse.service;

import com.prm392.be.labverse.dto.S3SignedUrlResponse;
import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import com.prm392.be.labverse.dto.paper.AddPaperRequest;
import com.prm392.be.labverse.dto.paper.AddPaperResponse;
import com.prm392.be.labverse.dto.paper.PaperInfoResponse;
import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.entity.Favorite;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.ReadingStatus;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.PaperErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.mapper.PaperMapper;
import com.prm392.be.labverse.repository.FavoriteRepository;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.ReadingStatusRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import com.prm392.be.labverse.util.FileStorageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FileStorageHelper fileStorageHelper;
    private final PaperMapper paperMapper;
    private final FavoriteRepository favoriteRepository;
    private final ReadingStatusRepository readingStatusRepository;

    @Override
    public S3SignedUrlResponse getUploadUrl(String key) {
        return new S3SignedUrlResponse(fileStorageHelper.generateUploadPaperUrl(key));
    }

    @Override
    public AddPaperResponse addPaper(AddPaperRequest request) {
        //get user thực hiện request hiện tại
//        User user = userRepository.getReferenceById(CurrentUserInfoUtil.getCurrentUserId());
        User user = userRepository.findByIdAndDeleteFlagFalse(CurrentUserInfoUtil.getCurrentUserId())
                .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));

        //tao object paper
        Paper paper = Paper.builder()
                .s3Key(request.s3Key())
                .user(user)
                .totalPage(request.totalPage())
                .authorName(request.authorName())
                .title(request.title())
                .publicationYear(request.publicationYear())
                .doi(request.doi())
                .build();
        // lưu object paper vào db (bao gồm cả key)
        paperRepository.save(paper);
        return new AddPaperResponse(paper.getId());
    }

    @Override
    public S3SignedUrlResponse getDownloadUrl(String s3Key) {
        //todo, kiểm tra paper truowcs ddax, ddax bij xoa chua chang hanj, hinh nhuw ham nay ko duoj suw dun
        String url = fileStorageHelper.generateDownloadFileUrl(s3Key);
        return new S3SignedUrlResponse(url);
    }

    @Override
    public PaperInfoResponse getPaperInfo(String id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        if (paper.isDeleteFlag()) throw new AppException(PaperErrorCode.PAPER_IS_DELETED);

        return paperMapper.toPaperInfoResponse(paper);
    }

    // ====== Dashboard/List ======
    @Override
    @Transactional(readOnly = true)
    public Page<PaperCardDTO> listForDashboard(String userId, String filter, Pageable pageable) {
        String f = (filter == null || filter.isBlank()) ? "recently_added" : filter;

        return switch (f) {
            case "recently_read" -> paperRepository.pageRecentlyRead(userId, pageable);
            case "favorites"     -> paperRepository.pageFavorites(userId, pageable);
            default              -> paperRepository.pageRecentlyAdded(userId, pageable);
        };
    }

    // ====== Progress ======
    @Override
    @Transactional
    public void updateReadingProgress(String userId, String paperId, int currentPage) {
        Paper paper = paperRepository.findByIdAndDeleteFlagFalse(paperId)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        int total = Math.max(paper.getTotalPage(), 0);
        int clamped = Math.max(0, Math.min(currentPage, total));

        ReadingStatus rs = readingStatusRepository.findByUser_IdAndPaper_Id(userId, paperId)
                .orElseGet(() -> {
                    User u = userRepository.findByIdAndDeleteFlagFalse(userId)
                            .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));
                    return ReadingStatus.builder()
                            .user(u)
                            .paper(paper)
                            .currentPage(0)
                            .build();
                });

        rs.setCurrentPage(clamped);
        rs.setLastReadAt(LocalDateTime.now());
        readingStatusRepository.save(rs);

        // Nếu chủ sở hữu paper == user → sync vào Paper.currentPage (tuỳ product logic)
        if (paper.getUser().getId().equals(userId)) {
            paper.setCurrentPage(clamped);
            paperRepository.save(paper);
        }
    }

    // ====== Favorite ======
    @Override
    @Transactional
    public void addFavorite(String userId, String paperId) {
        // đã tồn tại thì thôi
        if (favoriteRepository.existsByUser_IdAndPaper_Id(userId, paperId)) return;

        User u = userRepository.findByIdAndDeleteFlagFalse(userId)
                .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));
        Paper p = paperRepository.findByIdAndDeleteFlagFalse(paperId)
                .orElseThrow(() -> new AppException(PaperErrorCode.PAPER_NOT_FOUND));

        Favorite f = Favorite.builder().user(u).paper(p).build();
        favoriteRepository.save(f);
    }

    @Override
    @Transactional
    public void removeFavorite(String userId, String paperId) {
        favoriteRepository.deleteByUser_IdAndPaper_Id(userId, paperId);
    }

    public List<PaperSummaryDTO> listAllSummariesForUser(String userId){
        var list = paperRepository.findSummariesByOwner(userId);
//        log.info("listAllSummariesForUser userId={} -> {} items", userId, list.size());
        return list;
    }


    @Override
    public List<PaperInfoResponse> getMyPapersOfCurrentUser() {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AppException(UserErrorCode.UN_AUTHENTICATED);
        }

        return paperRepository
                .findByUser_IdAndDeleteFlagFalse(currentUserId)
                .stream()
                .map(paper -> PaperInfoResponse.builder()
                        .id(paper.getId())
                        .s3Key(paper.getS3Key())
                        .totalPage(paper.getTotalPage())
                        .currentPage(paper.getCurrentPage())
                        .authorName(paper.getAuthorName())
                        .title(paper.getTitle())
                        .publicationYear(paper.getPublicationYear())
                        .doi(paper.getDoi())
                        .build())
                .toList();
    }
}
