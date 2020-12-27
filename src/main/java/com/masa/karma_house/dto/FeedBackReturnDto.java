package com.masa.karma_house.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedBackReturnDto {
    private long id;
    private long taskLogId;
    private long tenantId;
    private String comment;
    private String timestamp;

    public FeedBackReturnDto( long taskLogId, long tenantId, String comment, String timestamp) {
        this.taskLogId = taskLogId;
        this.tenantId = tenantId;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}
