package com.masa.karma_house.repositories;

import com.masa.karma_house.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<User, Long> {
    User findByName(String userName);

    boolean existsByLogin(String login);

   // User findByLogin(String login);
}
