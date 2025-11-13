package com.prm392.be.labverse.repository;

import com.prm392.be.labverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    /** tìm tài khoản theo user id mà vẫn còn active     */
    Optional<User> findByIdAndDeleteFlagFalse(String id);
    Optional<User> findByEmailAndDeleteFlagFalse(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
