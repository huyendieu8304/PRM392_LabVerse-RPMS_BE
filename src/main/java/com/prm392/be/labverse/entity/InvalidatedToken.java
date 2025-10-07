package com.prm392.be.labverse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "InvalidatedToken", indexes = {
        @Index(name = "idx_access_token", columnList = "accessToken")
})
@Data
public class InvalidatedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String accessToken;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt = LocalDateTime.now();

}
