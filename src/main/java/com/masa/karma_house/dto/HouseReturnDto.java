package com.masa.karma_house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HouseReturnDto {
    private long id;
    private String name;
    private String creator;
}
