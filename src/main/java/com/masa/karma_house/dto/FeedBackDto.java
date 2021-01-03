package com.masa.karma_house.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedBackDto {

    private long taskLogId;
    private long tenantId;
    private String comment;
    private String timestamp;
}
