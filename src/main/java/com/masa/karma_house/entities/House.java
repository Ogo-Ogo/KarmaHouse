package com.masa.karma_house.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "house", indexes = {@Index(columnList = "name", unique = true)})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = { "id, name" })
public class House {
    public static final int START_SEQ = 1;

    @Id
    @SequenceGenerator(name = "house_seq", sequenceName = "house_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "house_seq")
    @Setter(value= AccessLevel.NONE)
    private long id;

    @NotNull
    private String name;

    public House(String name) {
        this.name =name;
    }
}
