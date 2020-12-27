package com.masa.karma_house.dto;

import com.masa.karma_house.entities.CommentTemplate;
import com.masa.karma_house.entities.TaskLog;
import com.masa.karma_house.entities.Tenant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
public class FeedBackDto {

    private long taskLogId;
    private long tenantId;
    private String comment;
    private String timestamp;
}
