package com.prm392.be.labverse.service.impl;

import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.dto.readinglist.CreateReadingListRequest;
import com.prm392.be.labverse.dto.readinglist.ImportPaperRequest;
import com.prm392.be.labverse.dto.readinglist.ReadingListItemDTO;
import com.prm392.be.labverse.dto.readinglist.ReadingListResponse;
import com.prm392.be.labverse.entity.Paper;
import com.prm392.be.labverse.entity.ReadingList;
import com.prm392.be.labverse.entity.ReadingListItem;
import com.prm392.be.labverse.entity.User;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.CommonErrorCode;
import com.prm392.be.labverse.exception.UserErrorCode;
import com.prm392.be.labverse.repository.PaperRepository;
import com.prm392.be.labverse.repository.ReadingListItemRepository;
import com.prm392.be.labverse.repository.ReadingListRepository;
import com.prm392.be.labverse.repository.UserRepository;
import com.prm392.be.labverse.service.PaperService;
import com.prm392.be.labverse.service.ReadingListService;
import com.prm392.be.labverse.util.CurrentUserInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingListServiceImpl implements ReadingListService {

    private final ReadingListRepository readingListRepo;
    private final UserRepository userRepo;
    private final ReadingListRepository listRepo;
    private final ReadingListItemRepository itemRepo;
    private final PaperRepository paperRepo;
    private final PaperService paperService;

    @Override
    @Transactional
    public ReadingListResponse create(CreateReadingListRequest req) {
        String currentUserId = CurrentUserInfoUtil.getCurrentUserId();

        User user = userRepo.findByIdAndDeleteFlagFalse(currentUserId)
                .orElseThrow(() -> new AppException(UserErrorCode.ACCOUNT_NOT_FOUND));

        // Không cho trùng tên (case-insensitive) trong phạm vi 1 user
        boolean duplicated = readingListRepo
                .existsByUser_IdAndNameIgnoreCaseAndDeleteFlagFalse(currentUserId, req.name());
        if (duplicated) {
            throw new AppException(UserErrorCode.INVALID_REQUEST); // hoặc ReadingListErrorCode.DUPLICATED_NAME nếu bạn có
        }

        ReadingList saved = readingListRepo.save(
                ReadingList.builder()
                        .user(user)
                        .name(req.name().trim())
                        .description(req.description() == null ? null : req.description().trim())
                        .build()
        );

        // record: (id, name, description, paperCount, createdAt)
        return new ReadingListResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                0,
                saved.getCreatedAt()
        );
    }


    // Service
    @Override
    @Transactional
    public ReadingListResponse rename(String id, String newName, String desc) {
        String uid = CurrentUserInfoUtil.getCurrentUserId();

        ReadingList list = readingListRepo.findByIdAndDeleteFlagFalse(id)
                .orElseThrow(() -> new AppException(UserErrorCode.RESOURCE_NOT_FOUND));
        if (!list.getUser().getId().equals(uid)) {
            throw new AppException(UserErrorCode.FORBIDDEN);
        }

        // check trùng tên theo user
        boolean dup = readingListRepo.existsByUser_IdAndNameIgnoreCaseAndDeleteFlagFalse(uid, newName);
        if (dup && !list.getName().equalsIgnoreCase(newName)) {
            throw new AppException(UserErrorCode.INVALID_REQUEST); // hoặc ReadingListErrorCode.DUPLICATED_NAME
        }

        list.setName(newName.trim());
        list.setDescription(desc == null ? null : desc.trim());

        ReadingList saved = readingListRepo.save(list);

        return new ReadingListResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getItems() == null ? 0 : saved.getItems().size(),
                saved.getCreatedAt()
        );
    }


    @Override
    @Transactional
    public void deleteList(String id) {
        if (!listRepo.existsById(id)) throw notFound("ReadingList", id);
        // orphanRemoval trên ReadingList.items sẽ tự xoá ReadingListItem; nếu chưa bật, repo item xoá theo listId
        listRepo.deleteById(id);
    }

    @Override
    @Transactional
    public ReadingListItemDTO addExistingPaper(String listId, String paperId, Integer position) {
        if (itemRepo.existsByReadingListIdAndPaperId(listId, paperId)) {
            throw new AppException(CommonErrorCode.DUPLICATE);
        }


        ReadingList list = listRepo.findById(listId)
                .orElseThrow(() -> notFound("ReadingList", listId));
        Paper paper = paperRepo.findById(paperId)
                .orElseThrow(() -> notFound("Paper", paperId));

        int pos = resolvePosition(listId, position);

        ReadingListItem item = new ReadingListItem();
        item.setReadingList(list);
        item.setPaper(paper);
        item.setPosition(pos);

        ReadingListItem saved = itemRepo.save(item);
        // TRẢ DTO – tránh serialize LAZY proxy
        return new ReadingListItemDTO(saved.getId(), saved.getPaper().getId(), saved.getPosition());
    }


//    @Override
//    @Transactional
//    public ReadingListItem importPaperIntoList(String listId, ImportPaperRequest req) {
//        // 1) Tạo Paper mới từ S3 key (đã upload bằng presigned URL)
//        Paper created = paperService.addPaperFromS3(
//                req.title(),
//                req.authors(),
//                req.abstractText(),
//                req.s3Key()
//        );
//
//        // 2) Gắn vào list
//        return addExistingPaper(listId, created.getId(), req.position());
//    }

    @Override
    @Transactional(readOnly = true)
    public List<Paper> getPapers(String listId) {
        if (!listRepo.existsById(listId)) throw notFound("ReadingList", listId);
        return itemRepo.findByReadingListIdOrderByPositionAsc(listId)
                .stream()
                .map(ReadingListItem::getPaper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removePaper(String listId, String paperId) {
        // Có thể kiểm tra tồn tại trước để trả 404 thân thiện hơn
        if (!listRepo.existsById(listId)) throw notFound("ReadingList", listId);
        if (!paperRepo.existsById(paperId)) throw notFound("Paper", paperId);

        int deleted = itemRepo.deleteByReadingListIdAndPaperId(listId, paperId);
        if (deleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not in list");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaperSummaryDTO> getPapersAsSummaries(String userId, String listId) {
        // đảm bảo list thuộc về user hiện tại
        ReadingList rl = readingListRepo.findByIdAndUser_Id(listId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading list not found"));

        List<ReadingListItem> items = itemRepo.findAllByReadingListOrderByPositionAsc(rl);

        return items.stream()
                .map(ReadingListItem::getPaper)
                .filter(Objects::nonNull)
                .map(p -> new PaperSummaryDTO(
                        p.getId(),
                        p.getTitle(),
                        p.getAuthorName(),
                        p.getJournalName(),
                        p.getTotalPage(),
                        p.getCurrentPage(),                         // progress
                        deriveStatus(p.getCurrentPage(), p.getTotalPage()), // status tính từ current/total
                        p.getCreatedAt(),
                        p.getUpdatedAt()                             // hoặc lastReadAt nếu bạn có
                ))
                .toList();
    }

    @Override
    @Transactional
    public void removePaper(String userId, String listId, String paperId) {
        ReadingList rl = readingListRepo.findByIdAndUser_Id(listId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        itemRepo.deleteByReadingList_IdAndPaper_Id(listId, paperId);
    }

    @Override
    public List<ReadingListResponse> listMine(String userId) {
        List<ReadingList> entities = readingListRepo.findByUserIdAndDeleteFlagFalseOrderByCreatedAtDesc(userId);
        return entities.stream()
                .map(e -> new ReadingListResponse(
                        e.getId(),
                        e.getName(),
                        e.getDescription(),
                        e.getItems() == null ? 0 : e.getItems().size(),
                        e.getCreatedAt()
                ))
                .toList();
    }

    // ---- helpers ----
    private int resolvePosition(String listId, Integer requested) {
        if (requested != null && requested > 0) return requested;
        // Nếu có query riêng findMaxPositionByReadingListId thì dùng, ở đây fallback = size + 1
        int size = itemRepo.findByReadingListIdOrderByPositionAsc(listId).size();
        return size + 1;
    }

    private ResponseStatusException notFound(String what, String id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found: " + id);
    }

    public ReadingListItemDTO toDTO(ReadingListItem item) {
        return new ReadingListItemDTO(
                item.getId(),
                item.getPaper().getId(),
                item.getPosition()
        );
    }

    private String deriveStatus(int currentPage, int totalPage) {
        if (totalPage > 0 && currentPage >= totalPage) return "DONE";
        if (currentPage <= 0) return "UNREAD";
        return "IN_PROGRESS";
    }




}
