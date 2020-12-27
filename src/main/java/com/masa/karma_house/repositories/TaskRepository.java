package com.masa.karma_house.repositories;
import com.masa.karma_house.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> findAllByHouseId(long id);
}
