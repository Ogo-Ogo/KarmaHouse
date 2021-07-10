package com.masa.karma_house;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.entities.*;
import com.masa.karma_house.services.IKarmaHouse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KarmaHouseApplicationTests {
    private static final String USER_NAME1 = "Ivan Kampo";
    private static final String USER_NAME2 = "Sara Banega";
    private static final String LOGIN1 = "IvKam150";
    private static final String EMAIL1 = "Ivy150@gmail.com";
    private static final String LOGIN2 = "Sarit89";
    private static final String EMAIL2 = "sarape@gmail.com";
    private static final String PASSWORD1 = "ivanito88";
    private static final String PASSWORD2 = "sara";
    private static final String HOUSE_NAME1 = "johny_house";
    private static final String TASK_NAME1 = "Make some food";
    private static final String TASK_NAME2 = "Pay electricity bill";
    private static final String  REGULARITY = "Daily";
    private static final String  DESCRIPTION1 = "Cook food for all tenants of the house";
    private static final String  DESCRIPTION2 = "Pay electricity bill on Israel Post";
    private static final long KARMA_SCORE1 = 50;
    private static final long KARMA_SCORE2 = 75;
    private static final int YEAR1 = 2018;
    private static final int MONTH1 = 12;
    private static final int DAY1 = 20;
    private static final int HOUR1 = 22;
    private static final int MINUTE1 = 50;
    private static final int SECONDS1 = 15;

    static UserRegisterDto user1 =new UserRegisterDto(USER_NAME1, LOGIN1, EMAIL1, PASSWORD1);
    static UserRegisterDto user2 =new UserRegisterDto(USER_NAME2, LOGIN2, EMAIL2, PASSWORD2);
    static HouseDto house1 = new HouseDto(HOUSE_NAME1, USER_NAME1);

    @Autowired
    IKarmaHouse service;

    @Test
    public void registerUserTest() {
        UserDto user = service.addUser(user1);
        Assertions.assertEquals(user.getName(), USER_NAME1);
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void getUserDataTest() throws IOException {
        UserDto user = service.addUser(user1);
        UserDto user2 =  service.getUserData("Ivan Kampo", user.getName());
        Assertions.assertEquals(user.getEmail(), user2.getEmail());
        service.removeUser(user.getName(), USER_NAME1);
    }


    @Test
    public void removeUserTest(){
        UserDto user = service.addUser(user1);
        UserDto user2 =  service.removeUser(user1.getName(), USER_NAME1);
        Assertions.assertEquals(user.getEmail(), user2.getEmail());
    }

    @Test
    public void addHouseTest(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        service.deleteHouse(house.getId(), user.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void editHouseTest(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        HouseReturnDto editedHouse = service.editHouse(house.getId(), HouseChangeDto.builder().name("MosheHouse").build(), user.getName());
        Assertions.assertEquals("MosheHouse", editedHouse.getName());
        service.deleteHouse(house.getId(), user.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void removeHouseTest(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        HouseReturnDto deletedHouse = service.deleteHouse(house.getId(), user.getName());
        Assertions.assertEquals(house.getName(), deletedHouse.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void getHouseTestKarma(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        long karma = service.getHouseKarma(house.getId());
        Assertions.assertEquals(0, karma);
        service.deleteHouse(house.getId(), user.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void getAllHousesTest(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        List<HouseReturnDto> houses = service.getAllHouses();
        Assertions.assertEquals(1, houses.size());
        service.deleteHouse(house.getId(), user.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void getHouseTest(){
        UserDto user = service.addUser(user1);
        HouseReturnDto house  = service.addHouse(house1, user.getName());
        Assertions.assertEquals(USER_NAME1, house.getCreator());
        HouseReturnDto receivedHouse = service.getHouse(house.getId());
        Assertions.assertEquals(house.getName(), receivedHouse.getName());
        service.deleteHouse(house.getId(), user.getName());
        service.removeUser(user.getName(), USER_NAME1);
    }

    @Test
    public void applyToBecomeMemberOfHouseTest(){
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto application = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        Assertions.assertEquals(application.getHouse().getId(), house.getId());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getAllApplicationsTest(){
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        List<ApplicationReturnDto> applications = service.getAllApplications(tenant.getId(), house.getId());
        Assertions.assertEquals(applications.size(), 1);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getApplicationTest(){
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        Assertions.assertEquals(application.getName(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }


    @Transactional
    @Test
    public void addTenantTest(){
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew =  service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        Assertions.assertEquals(USER_NAME2, tenantNew.getName());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getAllTenantsTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        Assertions.assertEquals(application.getName(), USER_NAME2);
        service.addTenant(tenant.getId(), house.getId(), application.getId(), application.getName(), Role.USER.toString());
        List<TenantDto> tenants = service.getAllTenants(house.getId());
        Assertions.assertEquals(1, tenants.size());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void editProfileTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew =  service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        Assertions.assertEquals(USER_NAME2, tenantNew.getName());
        log.error("tenant new " + tenantNew);
        TenantDto editedTenant = service.editProfile(tenantNew.getId(), TenantEditDto.builder().name(tenantNew.getName()).email("SaraPel@gmail.com").password(tenantNew.getPassword()).build());
        Assertions.assertEquals("SaraPel@gmail.com", editedTenant.getEmail());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void changeRoleTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TenantDto tenantWithNewRole = service.changeRole(tenantNew.getId(), tenant.getId(), userOne.getName(), Role.ADMIN_HOUSE.toString());
        Assertions.assertTrue(tenantWithNewRole.getRole().contains(Role.valueOf("ADMIN_HOUSE")));
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void deleteTenantProfileTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), application.getName(), Role.USER.toString());
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME1);
        Assertions.assertEquals(1, service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).size());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void addTaskTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task =  service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);

        Assertions.assertEquals(50, task.getKarma_score());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getAllTasksTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        List<TaskReturnDto> tasks = service.getAllTasks(house.getId(), tenantNew.getName());
        Assertions.assertEquals(1, tasks.size());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }
    @Transactional
    @Test
    public void getTaskTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskReturnDto taskCreated = service.getTask(task.getId(), house.getId(), USER_NAME2);
        Assertions.assertEquals(DESCRIPTION1, taskCreated.getDescription());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void editTaskTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskReturnDto taskEdited = service.editTask(task.getId(), USER_NAME2, TaskEditDto.builder().name(task.getName()
        ).epic_type(EpicType.URGENT_STAFF.toString()).regularity(task.getRegularity()).description(DESCRIPTION2)
        .karma_score(KARMA_SCORE2).build());
        Assertions.assertEquals(KARMA_SCORE2, taskEdited.getKarma_score());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }


    @Transactional
    @Test
    public void deleteTaskTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskReturnDto taskDeleted = service.deleteTask(task.getId(), task.getCreator());
        Assertions.assertEquals(USER_NAME2, taskDeleted.getCreator());
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void addTaskLogTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
        .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        Assertions.assertEquals(house.getName(), taskLog.getHouseReturnDto().getName());
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }
    @Transactional
    @Test
    public void getAllTaskLogsTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        List<TaskReturnDto> list = service.getAllTasks(house.getId(), USER_NAME1);
        Assertions.assertEquals(1, list.size());
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getTaskLogTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogGet = service.getTaskLog(taskLog.getId(), house.getId(), USER_NAME1);
        Assertions.assertEquals(0, taskLogGet.getComments().size());
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void deleteTaskLogTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogDel = service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        Assertions.assertEquals(taskLogDel.getId(), taskLog.getId());
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void addComment() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogWithComment = service.addComment(FeedBackDto.builder().taskLogId(taskLog.getId()).tenantId(taskLog.getTenantDto().getId())
        .comment(CommentTemplate.You_are_awesome.toString()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        Assertions.assertEquals(1, taskLogWithComment.getComments().size());
        service.deleteComment(taskLogWithComment.getComments().get(0).getId(), USER_NAME1);
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void deleteComment() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogWithComment = service.addComment(FeedBackDto.builder().taskLogId(taskLog.getId()).tenantId(taskLog.getTenantDto().getId())
                .comment(CommentTemplate.You_are_awesome.toString()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        service.deleteComment(taskLogWithComment.getComments().get(0).getId(), USER_NAME1);
        Assertions.assertEquals(0, service.getAllComments(taskLog.getId(), USER_NAME1).size());
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getAllCommentsTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogWithComment = service.addComment(FeedBackDto.builder().taskLogId(taskLog.getId()).tenantId(taskLog.getTenantDto().getId())
                .comment(CommentTemplate.You_are_awesome.toString()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        List<FeedBackReturnDto> comments = service.getAllComments(taskLogWithComment.getId(), USER_NAME1);
        Assertions.assertEquals(1, taskLogWithComment.getComments().size());
        service.deleteComment(taskLogWithComment.getComments().get(0).getId(), USER_NAME1);
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

    @Transactional
    @Test
    public void getCommentTest() {
        UserDto userOne = service.addUser(user1);
        UserDto userTwo = service.addUser(user2);
        HouseReturnDto house  = service.addHouse(house1, userOne.getName());
        ApplicationReturnDto applicationDto = service.applyToBecomeMemberOfHouse(userTwo.getId(), house.getId());
        TenantDto tenant = service.getAllTenants(house.getId()).stream().filter(c -> c.getName().equalsIgnoreCase(userOne.getName())).collect(Collectors.toList()).get(0);
        ApplicationReturnDto application = service.getApplication(tenant.getId(), house.getId(), applicationDto.getId());
        TenantDto tenantNew = service.addTenant(tenant.getId(), house.getId(), application.getId(), USER_NAME1, Role.USER.toString());
        TaskReturnDto task = service.addTask(TaskDto.builder().name(TASK_NAME1).house_id(house.getId()).tenant_id(tenantNew.getId())
                .epic_type(EpicType.DAILY_SHIT.toString()).regularity(REGULARITY).description(DESCRIPTION1)
                .karma_score(KARMA_SCORE1).build(), USER_NAME2);
        TaskLogReturnDto taskLog= service.addTaskLog(TaskLogDto.builder().house_id(house.getId()).task_id(task.getId())
                .tenant_id(tenant.getId()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        TaskLogReturnDto taskLogWithComment = service.addComment(FeedBackDto.builder().taskLogId(taskLog.getId()).tenantId(taskLog.getTenantDto().getId())
                .comment(CommentTemplate.You_are_awesome.toString()).timestamp(YEAR1+"-"+MONTH1+"-"+ DAY1 + ' '+ HOUR1+":"+MINUTE1+":"+SECONDS1).build(), USER_NAME1);
        FeedBackReturnDto comment = service.getComment(taskLogWithComment.getComments().get(0).getId(), USER_NAME1);
        Assertions.assertEquals(CommentTemplate.You_are_awesome.toString(), comment.getComment());
        service.deleteComment(taskLogWithComment.getComments().get(0).getId(), USER_NAME1);
        service.deleteTaskLog(taskLog.getId(), USER_NAME1);
        service.deleteTenantProfile(tenant.getId(), USER_NAME1);
        service.deleteTenantProfile(tenantNew.getId(), USER_NAME2);
        service.deleteHouse(house.getId(), userOne.getName());
        service.removeUser(userOne.getName(), USER_NAME1);
        service.removeUser(userTwo.getName(), USER_NAME2);
    }

}
