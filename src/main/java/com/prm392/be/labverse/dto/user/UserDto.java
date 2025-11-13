package com.prm392.be.labverse.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    public String id;
    public String full_name;
    public String email;
    public String phone_number;
    public Boolean gender;
    public String address;
    public Integer role_id;
    public Boolean delete_flag; // 0/1
    public LocalDateTime created_at;
    public LocalDateTime updated_at;
}
