package com.masa.karma_house.repositories;
import com.masa.karma_house.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Tenant findByNameAndEmail(String name, String email);

    List<Tenant> findAllByHouseId(long houseId);

    void deleteAllByHouseId(long houseId);
}
