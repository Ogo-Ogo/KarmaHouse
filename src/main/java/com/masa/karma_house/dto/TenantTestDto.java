package com.masa.karma_house.dto;

import com.masa.karma_house.entities.House;
import com.masa.karma_house.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TenantTestDto {

    private String name;
    private HouseDto house;
    private long karma_score;
    private String email;
    private String password;
    private Set<Role> role;
}
