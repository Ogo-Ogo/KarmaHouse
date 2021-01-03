package com.masa.karma_house.services;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.entities.*;
import com.masa.karma_house.repositories.*;
import com.masa.karma_house.security.TokenNotCorrespondsLoginException;
import com.masa.karma_house.security.UserExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service

public class IKarmaHouseImplementation implements IKarmaHouse {
    private HouseRepository houseRepository;
    private ApplicationRepository applicationRepository;
    private TenantRepository tenantRepository;
    private TaskRepository taskRepository;
    private TaskLogRepository taskLogRepository;
    private FeedBackRepository feedBackRepository;
    private AuthenticationRepository userRepository;
    private PasswordEncoder encoder;

    @Autowired
    public void setInjection(HouseRepository houseRepository, ApplicationRepository applicationRepository, TenantRepository tenantRepository,
                             TaskRepository taskRepository, TaskLogRepository taskLogRepository, FeedBackRepository feedBackRepository,
                             AuthenticationRepository userRepository, PasswordEncoder passwordEncoder) {
        this.houseRepository = houseRepository;
        this.applicationRepository = applicationRepository;
        this.tenantRepository = tenantRepository;
        this.taskRepository = taskRepository;
        this.taskLogRepository = taskLogRepository;
        this.feedBackRepository = feedBackRepository;
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto addUser(UserRegisterDto userRegisterDto) {
        if (userRepository.existsByLogin(userRegisterDto.getLogin())) {
            throw new UserExistsException("User with such login already exists");
        }
        User userAccount = new User(userRegisterDto.getName(), userRegisterDto.getLogin(),
                userRegisterDto.getEmail(), encoder.encode(userRegisterDto.getPassword()));
        userRepository.save(userAccount);
        return createUserDto(userAccount);
    }


    @Override
    public UserDto getUserData(String currentUserName, String name) {
        if (!name.equals(currentUserName)) {
            throw new TokenNotCorrespondsLoginException("User name not corresponds to token");
        }
        User user = userRepository.findByName(name);
        if (user != null) {
            return createUserDto(user);
        } else
            return null;
    }

    @Override
    public UserDto removeUser(String name, String currentUserName) {
        if (!name.equals(currentUserName)) {
            throw new TokenNotCorrespondsLoginException("User name not corresponds to token");
        }
        User userAccount = userRepository.findByName(name);
        if (userAccount != null) {
            userRepository.delete(userAccount);
            return createUserDto(userAccount);
        }
        return null;
    }

    private UserDto createUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getLogin(),
                user.getEmail(), user.getPassword());
    }

    @Override
    public HouseReturnDto addHouse(HouseDto houseDto, String name) {
        House house;
        if (houseRepository.findByName(houseDto.getName()) == null) {
            house = houseRepository.save(House.builder().name(houseDto.getName()).creator(houseDto.getCreator()).build());
            User user = userRepository.findByName(name);
            tenantRepository.save(Tenant.builder().name(user.getName()).house(house).karma_score(0L).email(user.getEmail())
                    .password(user.getPassword()).role(Role.ADMIN_HOUSE).build());
            return createHouseReturnDto(house);
        }
        return null;
    }

    @Override
    public HouseReturnDto editHouse(long houseId, HouseChangeDto houseDto, String userName) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house != null) {
            if (!userName.equals(house.getCreator())) {
                throw new TokenNotCorrespondsLoginException("User cannot edit house created by another user");
            }
            house.setName(houseDto.getName());
            House editedHouse = houseRepository.save(house);
            return createHouseReturnDto(editedHouse);
        }
        return null;
    }

    @Override
    @Transactional
    public HouseReturnDto deleteHouse(long houseId, String userName) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house != null) {
            if (!userName.equals(house.getCreator())) {
                throw new TokenNotCorrespondsLoginException("User cannot delete house created by another user");
            }
            tenantRepository.deleteAllByHouseId(houseId);
            houseRepository.delete(house);
            return createHouseReturnDto(house);
        }
        return null;
    }

    @Override
    public long getHouseKarma(long houseId) {
        List<Tenant> list = tenantRepository.findAllByHouseId(houseId);
        if (list.size() != 0) {
            return list.stream().mapToLong(Tenant::getKarma_score).sum();
        }
        return 0;
    }

    @Override
    public List<HouseReturnDto> getAllHouses() {
        List<House> houses = houseRepository.findAll();
        List<HouseReturnDto> list = new ArrayList<>();
        if (houses.size() != 0) {
            for (House house : houses) {
                list.add(createHouseReturnDto(house));
            }
            return list;
        }
        return null;
    }

    @Override
    public HouseReturnDto getHouse(long houseId) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house != null) {
            return createHouseReturnDto(house);
        }
        return null;
    }

    private HouseReturnDto createHouseReturnDto(House house) {
        return new HouseReturnDto(house.getId(), house.getName(), house.getCreator());
    }

    @Override
    public ApplicationReturnDto applyToBecomeMemberOfHouse(long userId, long houseId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            try {
                Application application = applicationRepository.save(Application.builder()
                        .name(user.getName()).email(user.getEmail()).password(user.getPassword()).house(
                                houseRepository.findById(houseId).orElse(null)).approved(false).build());
                return createApplicationReturnDto(application);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public List<ApplicationReturnDto> getAllApplications(long tenantId, long houseId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null && tenant.getRoles().stream().anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE"))
                && tenant.getHouse().getId() == houseId) {
            List<Application> applications = applicationRepository.findAllByHouseId(houseId);
            List<ApplicationReturnDto> list = new ArrayList<>();
            for (Application app : applications) {
                list.add(createApplicationReturnDto(app));
            }
            return list;
        }
        return null;

    }

    @Override
    public ApplicationReturnDto getApplication(long tenantId, long houseId, long id) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null && tenant.getRoles().stream().anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE"))
                && tenant.getHouse().getId() == houseId) {
            Application app = applicationRepository.findById(id).orElse(null);
            if (app != null) {
                return createApplicationReturnDto(app);
            } else return null;
        }
        return new ApplicationReturnDto();
    }

    private ApplicationReturnDto createApplicationReturnDto(Application application) {
        return new ApplicationReturnDto(application.getId(), application.getName(), application.getEmail(), application.getPassword(),
                createHouseReturnDto(application.getHouse()), application.isApproved());
    }

    @Override
    @Transactional
    public TenantDto addTenant(long tenantId, long houseId, long applicationId, String name, String role) {
        ApplicationReturnDto app = getApplication(tenantId, houseId, applicationId);
        if (app == null) {
            return null;
        }
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null && tenant.getRoles().stream().anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE"))
                && tenant.getHouse().getId() == houseId && tenant.getName().equalsIgnoreCase(name)) {
            tenant = tenantRepository.save(Tenant.builder().name(app.getName()).house(houseRepository.findById(app.getHouse().getId()).orElse(null))
                    .karma_score(0L).email(app.getEmail()).password(app.getPassword()).role(Role.valueOf(role)).build());
            applicationRepository.deleteById(applicationId);
            return createTenantDto(tenant);
        } else return new TenantDto();
    }

    @Override
    public List<TenantDto> getAllTenants(long houseId) {
        List<Tenant> tenants = tenantRepository.findAllByHouseId(houseId);
        List<TenantDto> listTenantDto = new ArrayList<>();
        for (Tenant t : tenants) {
            listTenantDto.add(createTenantDto(t));
        }
        return listTenantDto;
    }

    @Override
    public TenantDto editProfile(long tenantId, TenantEditDto tenantDto) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null) {
            tenant.setName(tenantDto.getName());
            tenant.setEmail(tenantDto.getEmail());
            String passwordHashed = encoder.encode(tenantDto.getPassword());
            tenant.setPassword(passwordHashed);
            Tenant editedTenant = tenantRepository.save(tenant);
            return createTenantDto(editedTenant);
        }
        return null;
    }

    @Override
    public TenantDto changeRole(long tenantId, long adminId, String name, String role) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        Tenant admin_house = tenantRepository.findById(adminId).orElse(null);
        if (!admin_house.getName().equalsIgnoreCase(name)) {
            return new TenantDto();
        }
        if (tenant != null && admin_house.getRoles().stream().anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE"))
                && tenant.getHouse().getId() == admin_house.getHouse().getId()) {
            Set<Role> set = new HashSet<>();
            set.add(Role.valueOf(role.toUpperCase()));
            tenant.setRoles(set);
            Tenant editedTenant = tenantRepository.save(tenant);
            return createTenantDto(editedTenant);
        }
        return null;
    }


    @Override
    public TenantDto deleteTenantProfile(long tenantId, String name) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        Tenant admin_house = tenantRepository.findByName(name);
        if (tenant != null && admin_house != null && (tenant.getName().equalsIgnoreCase(name)
                || admin_house.getRoles().stream()
                .anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE")))
                && (admin_house.getHouse().getId() == tenant.getHouse().getId())) {
            tenantRepository.delete(tenant);
            return createTenantDto(tenant);
        }
        return null;
    }

    private TenantDto createTenantDto(Tenant tenant) {
        return new TenantDto(tenant.getId(), tenant.getName(), tenant.getHouse(), tenant.getKarma_score(),
                tenant.getEmail(), tenant.getPassword(), tenant.getRoles());
    }

    @Override
    @Transactional
    public TaskReturnDto addTask(TaskDto taskDto, String name) {
        Tenant tenant = tenantRepository.findById(taskDto.getTenant_id()).orElse(null);
        House house = houseRepository.findById(taskDto.getHouse_id()).orElse(null);
        if (house == null || tenant == null) {
            return null;
        }
        if (tenant != null && house != null && tenant.getName().equalsIgnoreCase(name) &&
                tenant.getHouse().getId() == house.getId()) {
            Task task = taskRepository.save(Task.builder().name(taskDto.getName()).house(houseRepository.findById(taskDto.getHouse_id()).orElse(null))
                    .tenantName(tenantRepository.findById(taskDto.getTenant_id()).orElse(null).getName())
                    .epictype(EpicType.valueOf(taskDto.getEpic_type())).regularity(taskDto.getRegularity()).description(taskDto.getDescription())
                    .karma_score(taskDto.getKarma_score()).build());
            return createTaskReturnDto(task);
        } else return new TaskReturnDto();
    }

    @Override
    public List<TaskReturnDto> getAllTasks(long houseId, String name) {
        List<TenantDto> tenants = getAllTenants(houseId);
        if (tenants.size() != 0 && tenants.stream().map(c -> c.getName()).anyMatch(t -> t.equalsIgnoreCase(name))) {
            List<TaskReturnDto> listTaskDto = new ArrayList<>();
            List<Task> tasks = taskRepository.findAllByHouseId(houseId);
            for (Task task : tasks) {
                listTaskDto.add(createTaskReturnDto(task));
            }
            return listTaskDto;
        }
        return null;
    }

    @Override
    public TaskReturnDto getTask(long id, long houseId, String name) {
        List<TenantDto> tenants = getAllTenants(houseId);
        if (tenants.size() != 0 && tenants.stream().map(c -> c.getName()).anyMatch(t -> t.equalsIgnoreCase(name))) {
            Task task = taskRepository.findById(id).orElse(null);
            if (task != null) {
                return createTaskReturnDto(task);
            }
            return null;
        }
        return new TaskReturnDto();
    }

    @Override
    public TaskReturnDto editTask(long taskId, String name, TaskEditDto taskDto) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            if (!task.getTenantName().equalsIgnoreCase(name)) {
                return new TaskReturnDto();
            }
            task.setName(taskDto.getName());
            task.setEpictype(EpicType.valueOf(taskDto.getEpic_type()));
            task.setDescription(taskDto.getDescription());
            task.setRegularity(taskDto.getRegularity());
            task.setKarma_score(taskDto.getKarma_score());
            Task editedTask = taskRepository.save(task);
            return createTaskReturnDto(editedTask);
        }
        return null;
    }

    @Override
    public TaskReturnDto deleteTask(long id, String name) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            if (!task.getTenantName().equalsIgnoreCase(name)) {
                return new TaskReturnDto();
            }
            taskRepository.delete(task);
            return createTaskReturnDto(task);
        } else return null;
    }

    private TaskReturnDto createTaskReturnDto(Task task) {
        return new TaskReturnDto(task.getId(), task.getName(), createHouseReturnDto(task.getHouse()),
                task.getTenantName(), task.getEpictype().name(), task.getRegularity(), task.getDescription(), task.getKarma_score());
    }

    @Override
    public TaskLogReturnDto addTaskLog(TaskLogDto taskLogDto, String name) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Tenant tenant = tenantRepository.findById(taskLogDto.getTenant_id()).orElse(null);
        House house = houseRepository.findById(taskLogDto.getHouse_id()).orElse(null);
        if (house == null || tenant == null) {
            return null;
        } else if (tenant != null && house != null && tenant.getName().equalsIgnoreCase(name) &&
                tenant.getHouse().getId() == house.getId()) {
            TaskLog taskLog = taskLogRepository.save(new TaskLog(house,
                    taskRepository.findById(taskLogDto.getTask_id()).orElse(null),
                    tenant,
                    LocalDateTime.parse(taskLogDto.getTimestamp(), dtf), new ArrayList<>(), false));
            return createTaskLogReturnDto(taskLog);
        } else {
            return new TaskLogReturnDto();
        }
    }

    @Override
    public List<TaskLogReturnDto> getAllTaskLogs(long houseId, String name) {
        List<TenantDto> tenants = getAllTenants(houseId);
        if (tenants.size() != 0 && tenants.stream().map(c -> c.getName()).anyMatch(t -> t.equalsIgnoreCase(name))) {
            System.out.println("in get All");
            List<TaskLog> taskLogs = taskLogRepository.findAllByHouseId(houseId);
            if (taskLogs == null) {
                System.out.println("null");
                return null;
            }
            List<TaskLogReturnDto> listTaskDto = new ArrayList<>();
            for (TaskLog task : taskLogs) {
                listTaskDto.add(createTaskLogReturnDto(task));
            }
            return listTaskDto;
        } else return new ArrayList<>();
    }

    @Override
    public TaskLogReturnDto getTaskLog(long id, long houseId, String name) {
        List<TenantDto> tenants = getAllTenants(houseId);
        if (tenants.size() != 0 && tenants.stream().map(c -> c.getName()).anyMatch(t -> t.equalsIgnoreCase(name))) {
            TaskLog taskLog = taskLogRepository.findById(id).orElse(null);
            if (taskLog != null) {
                return createTaskLogReturnDto(taskLog);
            } else return null;
        } else return new TaskLogReturnDto();
    }

    @Transactional
    @Override
    public TaskLogReturnDto approveTaskLog(long taskLogId, String name) {
        TaskLog taskLog = taskLogRepository.findById(taskLogId).orElse(null);
        if (taskLog == null) {
            return null;
        } else if (taskLog.getTenant().getName().equalsIgnoreCase(name) && taskLog.getTenant().getRoles()
                .stream().anyMatch(r -> r.toString().equalsIgnoreCase("ADMIN_HOUSE"))) {
            taskLog.setApproved(true);
            taskLogRepository.save(taskLog);
            Tenant tenant = taskLog.getTenant();
            tenant.setKarma_score(taskLog.getTask().getKarma_score());
            tenantRepository.save(tenant);
            return createTaskLogReturnDto(taskLog);
        }
        return new TaskLogReturnDto();
    }

    @Override
    @Transactional
    public TaskLogReturnDto deleteTaskLog(long id, String name) {
        TaskLog taskLog = taskLogRepository.findById(id).orElse(null);
        if (taskLog != null) {
            if (!taskLog.getTenant().getName().equalsIgnoreCase(name)) {
                return new TaskLogReturnDto();
            }
            taskLogRepository.delete(taskLog);
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    private TaskLogReturnDto createTaskLogReturnDto(TaskLog taskLog) {
        return new TaskLogReturnDto(taskLog.getId(), createHouseReturnDto(taskLog.getHouse()),
                createTaskReturnDto(taskLog.getTask()), createTenantDto(taskLog.getTenant()),
                taskLog.getTimestamp().toString(), taskLog.getComments().stream().map(this::createFeedBackReturnDto).collect(Collectors.toList()), taskLog.isApproved());
    }

    @Override
    public TaskLogReturnDto addComment(FeedBackDto dto, String name) {
        TaskLog taskLog = taskLogRepository.findById(dto.getTaskLogId()).orElse(null);
        if (taskLog == null) {
            return null;
        }
        Tenant tenant = tenantRepository.findById(dto.getTenantId()).orElse(null);
        if (tenant != null && taskLog != null && tenant.getName().equalsIgnoreCase(name) &&
                tenant.getHouse().getId() == taskLog.getHouse().getId()) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            FeedBack feedBack = feedBackRepository.save(new FeedBack(taskLog, tenant, CommentTemplate.valueOf(dto.getComment()),
                    LocalDateTime.parse(dto.getTimestamp(), dtf)));
            taskLog.getComments().add(feedBack);
            taskLogRepository.save(taskLog);
            return createTaskLogReturnDto(taskLog);
        }
        return new TaskLogReturnDto();
    }

    @Override
    @Transactional
    public TaskLogReturnDto deleteComment(long id, String name) {
        FeedBack feedBack = feedBackRepository.findById(id).orElse(null);
        if (feedBack != null) {
            if (!feedBack.getTenant().getName().equalsIgnoreCase(name)) {
                return new TaskLogReturnDto();
            }
            TaskLog taskLog = taskLogRepository.findById(feedBack.getTask_log().getId()).orElse(null);
            taskLog.getComments().remove(feedBack);
            feedBackRepository.delete(feedBack);
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    public List<FeedBackReturnDto> getAllComments(long taskLogId, String name) {
        TaskLog taskLog = taskLogRepository.findById(taskLogId).orElse(null);
        if (taskLog == null) {
            return null;
        }
        if (taskLog != null && tenantRepository.findAllByHouseId(taskLog.getHouse().getId()).stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {

            List<FeedBack> list = taskLog.getComments();
            return list.stream().map(this::createFeedBackReturnDto).collect(Collectors.toList());
        } else return new ArrayList<>();
    }

    @Override
    public FeedBackReturnDto getComment(long id, String name) {
        FeedBack feedBack = feedBackRepository.findById(id).orElse(null);
        if (feedBack == null) {
            return null;
        }
        if (feedBack != null && tenantRepository.findAllByHouseId(feedBack.getTask_log().getHouse().getId()).stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            return createFeedBackReturnDto(feedBack);
        }
        return new FeedBackReturnDto();
    }

    private FeedBackReturnDto createFeedBackReturnDto(FeedBack feedBack) {
        return new FeedBackReturnDto(feedBack.getId(), feedBack.getTask_log().getId(),
                feedBack.getTenant().getId(), feedBack.getComment().toString(), feedBack.getTimestamp().toString());
    }
}
