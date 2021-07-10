package com.masa.karma_house.dto;

import com.masa.karma_house.entities.House;
import com.masa.karma_house.entities.Role;
import lombok.*;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TenantEditDto {

    private String name;
    private String email;
    private String password;
}
