package com.masa.karma_house.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "house", indexes = {@Index(columnList = "name", unique = true)})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = { "id, name" })
public class House {
    public static final int START_SEQ = 1;


    @Id
    @SequenceGenerator(name = "house_seq", sequenceName = "house_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "house_seq")
    private long id;

    @NotNull
    private String name;

    @Column(name = "creator")
    private String creator;

}
