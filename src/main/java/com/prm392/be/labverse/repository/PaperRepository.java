package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperRepository extends JpaRepository<Paper, String> {

}
