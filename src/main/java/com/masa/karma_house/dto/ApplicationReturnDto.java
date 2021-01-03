package com.masa.karma_house.dto;

import com.masa.karma_house.entities.House;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ApplicationReturnDto {
    private long id;
    private String name;
    private String email;
    private String password;
    private HouseReturnDto house;
    private boolean approved;
}
