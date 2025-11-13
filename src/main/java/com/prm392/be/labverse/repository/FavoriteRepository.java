package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.Favorite;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    boolean existsByUserIdAndPaperId(String userId, String paperId);
    void deleteByUserIdAndPaperId(String userId, String paperId);

    @Query("""
        select f from Favorite f
        join fetch f.paper p
        where f.user.id = :userId
        order by f.createdAt desc
    """)
    Page<Favorite> pageByUser(@Param("userId") String userId, Pageable pageable);
    boolean existsByUser_IdAndPaper_Id(String userId, String paperId);
    void deleteByUser_IdAndPaper_Id(String userId, String paperId);
}
