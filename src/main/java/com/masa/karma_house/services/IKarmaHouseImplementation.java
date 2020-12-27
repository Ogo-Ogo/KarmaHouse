package com.masa.karma_house.services;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.entities.*;
import com.masa.karma_house.repositories.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class IKarmaHouseImplementation implements IKarmaHouse {
    private HouseRepository houseRepository;
    private ApplicationRepository applicationRepository;
    private TenantRepository tenantRepository;
    private TaskRepository taskRepository;
    private TaskLogRepository taskLogRepository;
    private FeedBackRepository feedBackRepository;

    @Autowired
    public void setInjection(HouseRepository houseRepository, ApplicationRepository applicationRepository, TenantRepository tenantRepository,
                             TaskRepository taskRepository, TaskLogRepository taskLogRepository, FeedBackRepository feedBackRepository) {
        this.houseRepository = houseRepository;
        this.applicationRepository = applicationRepository;
        this.tenantRepository = tenantRepository;
        this.taskRepository = taskRepository;
        this.taskLogRepository = taskLogRepository;
        this.feedBackRepository = feedBackRepository;
    }

    @Override
    public HouseReturnDto addHouse(HouseDto houseDto) {
        House house;
        if (houseRepository.findByName(houseDto.getName()) == null) {
            house = houseRepository.save(new House(houseDto.getName()));
            return createHouseReturnDto(house);
        }
        return null;
    }

    @Override
    public HouseReturnDto editHouse(long houseId, HouseDto houseDto) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house != null) {
            house.setName(houseDto.getName());
            House editedHouse = houseRepository.save(house);
            return createHouseReturnDto(editedHouse);
        }
        return null;
    }

    @Override
    @Transactional
    public HouseReturnDto deleteHouse(long houseId) {
        House house = houseRepository.findById(houseId).orElse(null);
        if (house != null) {
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
        return new HouseReturnDto(house.getId(), house.getName());
    }

    @Override
    public ApplicationReturnDto applyToBecomeMemberOfHouse(ApplicationDto applicationDto) {
        String passwordHashed = BCrypt.hashpw(applicationDto.getPassword(), BCrypt.gensalt());
        try {
            Application application = applicationRepository.save(new Application(applicationDto.getName(),
                    applicationDto.getEmail(), passwordHashed,
                    houseRepository.findByName(applicationDto.getHouseName()), false));
            return createApplicationReturnDto(application);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public List<ApplicationReturnDto> getAllApplications(long houseId) {
        List<Application> applications = applicationRepository.findAllByHouseId(houseId);
        List<ApplicationReturnDto> list = new ArrayList<>();
        for (Application app : applications) {
            list.add(createApplicationReturnDto(app));
        }
        return list;
    }

    @Override
    public ApplicationReturnDto getApplication(long id) {
        Application app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            return createApplicationReturnDto(app);
        }
        return null;
    }

    private ApplicationReturnDto createApplicationReturnDto(Application application) {
        return new ApplicationReturnDto(application.getId(), application.getName(), application.getEmail(), application.getPassword(),
                createHouseReturnDto(application.getHouse()), application.isApproved());
    }

    @Override
    public TenantDto addTenant(long applicationId, String role) {
        ApplicationReturnDto app = getApplication(applicationId);
        Tenant tenant = tenantRepository.findByNameAndEmail(app.getName(), app.getEmail());
        if (tenant != null) {
            return null;
        } else {
            tenant = tenantRepository.save(new Tenant(app.getName(), houseRepository.findById(app.getHouse().getId()).orElse(null), 0L, app.getEmail(),
                    app.getPassword(), role.toUpperCase()));
            applicationRepository.deleteById(applicationId);
            return createTenantDto(tenant);
        }
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
            String passwordHashed = BCrypt.hashpw(tenantDto.getPassword(), BCrypt.gensalt());
            tenant.setPassword(passwordHashed);
            Tenant editedTenant = tenantRepository.save(tenant);
            return createTenantDto(editedTenant);
        }
        return null;
    }

    @Override
    public TenantDto changeRole(long tenantId, String role) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null) {
            Set<Role> set = new HashSet<>();
            set.add(Role.valueOf(role.toUpperCase()));
            tenant.setRoles(set);
            Tenant editedTenant = tenantRepository.save(tenant);
            return createTenantDto(editedTenant);
        }
        return null;
    }

    @Override
    public TenantDto deleteTenantByAdminHouse(long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant != null) {
            tenantRepository.delete(tenant);
            return createTenantDto(tenant);
        }
        return null;
    }

    @Override
    public TenantDto deleteTenantProfile(long tenantId) {
        return deleteTenantByAdminHouse(tenantId);
    }

    private TenantDto createTenantDto(Tenant tenant) {
        return new TenantDto(tenant.getId(), tenant.getName(), tenant.getHouse(), tenant.getKarma_score(),
                tenant.getEmail(), tenant.getPassword(), tenant.getRoles());
    }

    @Override
    @Transactional
    public TaskReturnDto addTask(TaskDto taskDto) {
        Task task = taskRepository.save(new Task(taskDto.getName(), houseRepository.findById(taskDto.getHouse_id()).orElse(null),
                tenantRepository.findById(taskDto.getTenant_id()).orElse(null).getName(), EpicType.valueOf(taskDto.getEpic_type()), taskDto.getRegularity(), taskDto.getDescription(), taskDto.getKarma_score()));
        return createTaskReturnDto(task);
    }

    @Override
    public List<TaskReturnDto> getAllTasks(long id) {
        List<Task> tasks = taskRepository.findAllByHouseId(id);
        List<TaskReturnDto> listTaskDto = new ArrayList<>();
        for (Task task : tasks) {
            listTaskDto.add(createTaskReturnDto(task));
        }
        return listTaskDto;
    }

    @Override
    public TaskReturnDto getTask(long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            return createTaskReturnDto(task);
        }
        return null;
    }

    @Override
    public TaskReturnDto editTask(long taskId, TaskEditDto taskDto) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
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
    public TaskReturnDto deleteTask(long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            taskRepository.delete(task);
            return createTaskReturnDto(task);
        } else return null;
    }

    private TaskReturnDto createTaskReturnDto(Task task) {
        return new TaskReturnDto(task.getId(), task.getName(), createHouseReturnDto(task.getHouse()),
                task.getTenantName(), task.getEpictype().name(), task.getRegularity(), task.getDescription(), task.getKarma_score());
    }

    @Override
    public TaskLogReturnDto addTaskLog(TaskLogDto taskLogDto) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TaskLog taskLog = taskLogRepository.save(new TaskLog(houseRepository.findById(taskLogDto.getHouse_id()).orElse(null),
                taskRepository.findById(taskLogDto.getTask_id()).orElse(null),
                tenantRepository.findById(taskLogDto.getTenant_id()).orElse(null),
                LocalDateTime.parse(taskLogDto.getTimestamp(), dtf), new ArrayList<>(), false));
        if (taskLog != null) {
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    public List<TaskLogReturnDto> getAllTaskLogs(long houseId) {
        List<TaskLog> taskLogs = taskLogRepository.findAllByHouseId(houseId);
        List<TaskLogReturnDto> listTaskDto = new ArrayList<>();
        for (TaskLog task : taskLogs) {
            listTaskDto.add(createTaskLogReturnDto(task));
        }
        return listTaskDto;
    }

    @Override
    public TaskLogReturnDto getTaskLog(long id) {
        TaskLog taskLog = taskLogRepository.findById(id).orElse(null);
        if (taskLog != null) {
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    public TaskLogReturnDto approveTaskLog(long taskLogId) {
        TaskLog taskLog = taskLogRepository.findById(taskLogId).orElse(null);
        if (taskLog != null) {
            taskLog.setApproved(true);
            taskLogRepository.save(taskLog);
            Tenant tenant = taskLog.getTenant();
            tenant.setKarma_score(taskLog.getTask().getKarma_score());
            tenantRepository.save(tenant);
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    @Transactional
    public TaskLogReturnDto deleteTaskLog(long id) {
        TaskLog taskLog = taskLogRepository.findById(id).orElse(null);
        if (taskLog != null) {
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
    public TaskLogReturnDto addComment(FeedBackDto dto) {
        TaskLog taskLog = taskLogRepository.findById(dto.getTaskLogId()).orElse(null);
        if (taskLog != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            FeedBack feedBack = feedBackRepository.save(new FeedBack(taskLogRepository.findById(dto.getTaskLogId()).orElse(null), tenantRepository.findById(dto.getTenantId()).orElse(null), CommentTemplate.valueOf(dto.getComment()),
                    LocalDateTime.parse(dto.getTimestamp(), dtf)));
            taskLog.getComments().add(feedBack);
            taskLogRepository.save(taskLog);
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    @Transactional
    public TaskLogReturnDto deleteComment(long id) {
        FeedBack feedBack = feedBackRepository.findById(id).orElse(null);
        if (feedBack != null) {
            TaskLog taskLog = taskLogRepository.findById(feedBack.getTask_log().getId()).orElse(null);
            taskLog.getComments().remove(feedBack);
            feedBackRepository.delete(feedBack);
            return createTaskLogReturnDto(taskLog);
        }
        return null;
    }

    @Override
    public List<FeedBackReturnDto> getAllComments(long taskLogId) {
        TaskLog taskLog = taskLogRepository.findById(taskLogId).orElse(null);
        if (taskLog != null) {
            List<FeedBack> list = taskLog.getComments();
            return list.stream().map(this::createFeedBackReturnDto).collect(Collectors.toList());
        } else return null;
    }

    @Override
    public FeedBackReturnDto getComment(long id) {
        FeedBack feedBack = feedBackRepository.findById(id).orElse(null);
        if (feedBack != null) {
            return createFeedBackReturnDto(feedBack);
        }
        return null;
    }

    private FeedBackReturnDto createFeedBackReturnDto(FeedBack feedBack) {
        return new FeedBackReturnDto(feedBack.getId(), feedBack.getTask_log().getId(),
                feedBack.getTenant().getId(), feedBack.getComment().toString(), feedBack.getTimestamp().toString());
    }
}
