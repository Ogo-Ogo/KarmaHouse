package com.masa.karma_house.entities;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "task", indexes = {@Index(columnList = "name", unique = true), @Index(columnList = "karma_score", unique = true), @Index(columnList = "epic_type") })
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@EqualsAndHashCode(of = { "id, name" })
public class Task {
    public static final int START_SEQ = 1;

    @Id
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @Setter(value=AccessLevel.NONE)
    private long id;

    @NotNull
    private String name;

    @JoinColumn(name = "house_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private House house;

    @Column(name = "creator")
    private String tenantName;

    @Enumerated(EnumType.STRING)
    @Column(name = "epic_type", nullable = false)
    private EpicType epictype;

    @NotNull
    private String regularity;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @Column(name = "karma_score", nullable = false)
    @NotNull
    @Range(min = 10, max = 5000)
    private long karma_score;

}
