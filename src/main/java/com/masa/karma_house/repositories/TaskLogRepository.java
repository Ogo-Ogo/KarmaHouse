package com.masa.karma_house.repositories;

import com.masa.karma_house.entities.TaskLog;
import com.masa.karma_house.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    List<TaskLog> findAllByHouseId(long houseId);

    Tenant findByTenantName(String name);
}
