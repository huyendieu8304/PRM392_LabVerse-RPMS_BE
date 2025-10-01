package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.constant.ERole;
import com.prm392.be.labverse.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
