package com.masa.karma_house.dto;


import com.masa.karma_house.entities.FeedBack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TaskLogReturnDto {
    private long id;
    private HouseReturnDto houseReturnDto;
    private TaskReturnDto taskReturnDto;
    private TenantDto tenantDto;
    private String timestamp;
    private List<FeedBackReturnDto> comments;
    private boolean approved;

}
