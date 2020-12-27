package com.masa.karma_house.dto;

import lombok.Getter;

@Getter
public class TaskLogDto {

    private long house_id;
    private long task_id;
    private long tenant_id;
    private String timestamp;
}
