package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.dto.dashboard.PaperCardDTO;
import com.prm392.be.labverse.dto.paper.PaperSummaryDTO;
import com.prm392.be.labverse.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaperRepository extends JpaRepository<Paper, String> {
    Optional<Paper> findByIdAndDeleteFlagFalse(String id);
    List<Paper> findByUser_IdAndDeleteFlagFalse(String userId);
    // recently_added: dựa createdAt của Paper
    @Query("""
select new com.prm392.be.labverse.dto.dashboard.PaperCardDTO(
  p.id, p.title, p.authorName, p.journalName,
  coalesce(rs.currentPage, 0), p.totalPage,
  case when f.id is not null then true else false end,
  p.createdAt, rs.lastReadAt
)
from Paper p
left join ReadingStatus rs on rs.paper = p and rs.user.id = :userId
left join Favorite f on f.paper = p and f.user.id = :userId
where p.user.id = :userId and p.deleteFlag = false
order by p.createdAt desc
""")
    Page<PaperCardDTO> pageRecentlyAdded(@Param("userId") String userId, Pageable pageable);

    @Query("""
select new com.prm392.be.labverse.dto.dashboard.PaperCardDTO(
  p.id, p.title, p.authorName, p.journalName,
  coalesce(rs.currentPage, 0), p.totalPage,
  case when f.id is not null then true else false end,
  p.createdAt, rs.lastReadAt
)
from ReadingStatus rs
join rs.paper p
left join Favorite f on f.paper = p and f.user.id = :userId
where rs.user.id = :userId and p.deleteFlag = false
order by rs.lastReadAt desc nulls last
""")
    Page<PaperCardDTO> pageRecentlyRead(@Param("userId") String userId, Pageable pageable);

    @Query("""
select new com.prm392.be.labverse.dto.dashboard.PaperCardDTO(
  p.id, p.title, p.authorName, p.journalName,
  coalesce(rs.currentPage, 0), p.totalPage,
  true,
  p.createdAt, rs.lastReadAt
)
from Favorite f
join f.paper p
left join ReadingStatus rs on rs.paper = p and rs.user.id = :userId
where f.user.id = :userId and p.deleteFlag = false
order by f.createdAt desc
""")
    Page<PaperCardDTO> pageFavorites(@Param("userId") String userId, Pageable pageable);

    Page<Paper> findByUser_IdAndDeleteFlagFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    @Query("""
        select new com.prm392.be.labverse.dto.paper.PaperSummaryDTO(
            p.id,
            p.title,
            p.authorName,
            p.journalName,
            p.totalPage,
            coalesce(rs.currentPage, 0),
            case 
                when coalesce(rs.currentPage, 0) = 0 then 'UNREAD'
                when coalesce(rs.currentPage, 0) >= p.totalPage then 'DONE'
                else 'IN_PROGRESS'
            end,
            p.createdAt,
            p.updatedAt
        )
        from Paper p
        left join ReadingStatus rs 
            on rs.paper = p 
           and rs.user.id = :userId
        where p.user.id = :userId 
          and (p.deleteFlag = false or p.deleteFlag is null)
        order by p.createdAt desc
        """)
    List<PaperSummaryDTO> findSummariesByOwner(@org.springframework.data.repository.query.Param("userId") String userId);
}
