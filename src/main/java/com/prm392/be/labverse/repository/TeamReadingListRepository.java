package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.TeamReadingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamReadingListRepository extends JpaRepository<TeamReadingList, String> {

    List<TeamReadingList> findByTeam_IdAndDeleteFlagFalse(String teamId);
}