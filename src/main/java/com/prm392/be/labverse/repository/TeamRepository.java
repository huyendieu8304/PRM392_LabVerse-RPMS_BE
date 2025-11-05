package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String> {
    List<Team> findByCreatedBy_Id(String createdById);
}
