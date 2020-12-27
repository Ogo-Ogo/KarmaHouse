package com.masa.karma_house.dto;
import com.masa.karma_house.entities.EpicType;
import lombok.Getter;


@Getter
public class TaskDto {
    private String name;
    private long house_id;
    private long tenant_id;
    private String epic_type;
    private String regularity;
    private String description;
    private long karma_score;
}
