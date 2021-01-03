package com.masa.karma_house.dto;

import com.masa.karma_house.entities.House;
import com.masa.karma_house.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantEditDto {

    private String name;
    private String email;
    private String password;
}
