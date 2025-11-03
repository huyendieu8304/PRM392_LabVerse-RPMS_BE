package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {
    List<Membership> findByTeam_Id(String teamId);
    List<Membership> findByUserId_Id(String userId);
    boolean existsByTeam_IdAndUserId_Id(String teamId, String userId);
    void deleteByTeam_Id(String teamId);
    Optional<Membership> findByTeam_IdAndUserId_Id(String teamId, String userId);
}
