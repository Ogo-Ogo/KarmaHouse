package com.masa.karma_house.services;

import com.masa.karma_house.dto.*;

import java.util.List;

public interface IKarmaHouse {
    HouseReturnDto addHouse(HouseDto houseDto);
    HouseReturnDto editHouse(long houseId, HouseDto houseDto);
    HouseReturnDto deleteHouse(long houseId);
    long getHouseKarma(long houseId);
    List<HouseReturnDto> getAllHouses();
    HouseReturnDto getHouse(long houseId);

    ApplicationReturnDto applyToBecomeMemberOfHouse(ApplicationDto applicationDto);
    List<ApplicationReturnDto> getAllApplications(long houseId);
    ApplicationReturnDto getApplication(long id);
    TenantDto addTenant(long applicationId, String role);

    List<TenantDto> getAllTenants(long houseid);
    TenantDto editProfile(long tenantId, TenantEditDto tenantDto);
    TenantDto changeRole(long tenantId, String role);
    TenantDto deleteTenantByAdminHouse(long tenantId);
    TenantDto deleteTenantProfile(long tenantId);

    TaskReturnDto addTask(TaskDto taskDto);
    List<TaskReturnDto> getAllTasks(long houseId);
    TaskReturnDto getTask(long id);
    TaskReturnDto editTask(long taskId, TaskEditDto taskDto);
    TaskReturnDto deleteTask(long id);

    TaskLogReturnDto addTaskLog(TaskLogDto taskLogDto);
    List<TaskLogReturnDto> getAllTaskLogs(long houseId);
    TaskLogReturnDto getTaskLog(long id);
    TaskLogReturnDto approveTaskLog(long taskLogId);
    TaskLogReturnDto deleteTaskLog(long id);

    TaskLogReturnDto addComment(FeedBackDto dto);
    TaskLogReturnDto deleteComment(long id);
    List<FeedBackReturnDto> getAllComments(long taskLogId);
    FeedBackReturnDto getComment(long id);



}
