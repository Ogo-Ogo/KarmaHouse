package com.masa.karma_house.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.masa.karma_house.entities.House;
import com.masa.karma_house.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TenantDto {

    private long id;
    private String name;
    private House house;
    private long karma_score;
    private String email;
    private String password;
    private Set<Role> role;
}
