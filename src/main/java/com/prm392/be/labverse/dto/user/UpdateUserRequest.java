package com.prm392.be.labverse.dto.user;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateUserRequest {
    public String full_name;
    public String phone_number;
    public Boolean gender;
    public String address;
}
