package com.prm392.be.labverse.entity;

import com.prm392.be.labverse.constant.ERole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING) //to save the name as String in db
    ERole name;
}
