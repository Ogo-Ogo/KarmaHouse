package com.masa.karma_house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApplicationDto {
    private String name;
    private String email;
    private String password;
    private String houseName;
}
