package com.masa.karma_house.dto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class TaskEditDto {
    private String name;
    private String epic_type;
    private String regularity;
    private String description;
    private long karma_score;
}
