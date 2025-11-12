package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.ReadingStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, String> {
    Optional<ReadingStatus> findByUserIdAndPaperId(String userId, String paperId);

    @Query("""
    select rs from ReadingStatus rs
    join fetch rs.paper p
    where rs.user.id = :userId
    order by rs.lastReadAt desc nulls last
  """)
    Page<ReadingStatus> pageByUserForRecentlyRead(@Param("userId") String userId, Pageable pageable);

    @Query("""
        select rs from ReadingStatus rs
        where rs.user.id = :userId and rs.paper.id in :paperIds
    """)
    List<ReadingStatus> findByUserAndPaperIds(@Param("userId") String userId, @Param("paperIds") List<String> paperIds);
    Optional<ReadingStatus> findByUser_IdAndPaper_Id(String userId, String paperId);
}
