package com.masa.karma_house.repositories;

import com.masa.karma_house.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository  extends JpaRepository<House, Long> {
    House findByName(String name);

  //  boolean existsByName(String name);
}
