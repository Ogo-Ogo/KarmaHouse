package com.masa.karma_house.repositories;

import com.masa.karma_house.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByHouseId(long houseId);
}
