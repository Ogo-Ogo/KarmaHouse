package com.masa.karma_house.dto;

import com.masa.karma_house.entities.EpicType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskReturnDto {
    private long id;
    private String name;
    private HouseReturnDto house;
    private String creator;
    private String epic_type;
    private String regularity;
    private String description;
    private long karma_score;
}
