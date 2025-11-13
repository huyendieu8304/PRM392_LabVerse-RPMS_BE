package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.ReadingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingListRepository extends JpaRepository<ReadingList, String> {

    // tìm 1 list theo id, chưa bị xóa mềm
    Optional<ReadingList> findByIdAndDeleteFlagFalse(String  id);

    // tìm 1 list theo id + đúng owner + chưa xóa mềm (khuyến nghị dùng để đảm bảo quyền)
    Optional<ReadingList> findByIdAndUser_IdAndDeleteFlagFalse(String  id, String userId);

    // kiểm tra trùng tên trong phạm vi 1 user (không phân biệt hoa/thường)
    boolean existsByUser_IdAndNameIgnoreCaseAndDeleteFlagFalse(String userId, String name);

    // kiểm tra trùng tên khi rename (loại trừ chính nó)
    boolean existsByUser_IdAndNameIgnoreCaseAndDeleteFlagFalseAndIdNot(String userId, String name, String id);
    Optional<ReadingList> findByIdAndUser_Id(String id, String userId);

    List<ReadingList> findByUserIdAndDeleteFlagFalseOrderByCreatedAtDesc(String userId);

}
