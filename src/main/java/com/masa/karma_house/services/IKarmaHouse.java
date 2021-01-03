package com.masa.karma_house.services;

import com.masa.karma_house.dto.*;
import org.springframework.security.core.userdetails.UserDetails;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IKarmaHouse {
    UserDto addUser(UserRegisterDto userRegisterDto);
    UserDto getUserData(String currentUser, String name) throws IOException;
    UserDto removeUser(String login, String name);

    HouseReturnDto addHouse(HouseDto houseDto, String userName);
    HouseReturnDto editHouse(long houseId, HouseChangeDto houseDto, String userName);
    HouseReturnDto deleteHouse(long houseId, String userName);
    long getHouseKarma(long houseId);
    List<HouseReturnDto> getAllHouses();
    HouseReturnDto getHouse(long houseId);

    ApplicationReturnDto applyToBecomeMemberOfHouse(long userId, long applicationDto);
    List<ApplicationReturnDto> getAllApplications(long tenantId, long houseId);
    ApplicationReturnDto getApplication(long tenantId, long houseId, long id);
    TenantDto addTenant(long tenantId, long houseId, long applicationId, String name, String role);

    List<TenantDto> getAllTenants(long houseid);
    TenantDto editProfile(long tenantId, TenantEditDto tenantDto);
    TenantDto changeRole(long tenantId, long adminId, String name, String role);
    TenantDto deleteTenantProfile(long tenantId, String name);

    TaskReturnDto addTask(TaskDto taskDto, String name);
    List<TaskReturnDto> getAllTasks(long houseId, String name);
    TaskReturnDto getTask(long id, long houseId, String name);
    TaskReturnDto editTask(long taskId, String name, TaskEditDto taskDto);
    TaskReturnDto deleteTask(long id, String name);

    TaskLogReturnDto addTaskLog(TaskLogDto taskLogDto, String name);
    List<TaskLogReturnDto> getAllTaskLogs(long houseId, String name);
    TaskLogReturnDto getTaskLog(long id, long houseId, String name);
    TaskLogReturnDto approveTaskLog(long taskLogId, String name);
    TaskLogReturnDto deleteTaskLog(long id, String name);

    TaskLogReturnDto addComment(FeedBackDto dto, String name);
    TaskLogReturnDto deleteComment(long id, String name);
    List<FeedBackReturnDto> getAllComments(long taskLogId, String name);
    FeedBackReturnDto getComment(long id, String name);



}
