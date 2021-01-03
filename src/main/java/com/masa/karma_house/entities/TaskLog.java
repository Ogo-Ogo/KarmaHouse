package com.masa.karma_house.entities;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_log", indexes = {@Index(columnList = "date_time")})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = { "id, name" })

public class TaskLog {
    public static final int START_SEQ = 1;

    @Id
    @SequenceGenerator(name = "tasklog_seq", sequenceName = "tasklog_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasklog_seq")
    @Setter(value=AccessLevel.NONE)
    private long id;

    @JoinColumn(name = "house_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private House house;

    @JoinColumn(name = "task_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(name = "date_time", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task_log")//, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    private List<FeedBack> comments;

    @Column(name = "approved", nullable = false, columnDefinition = "bool default false")
    private boolean approved;


    public TaskLog(House house, Task task, Tenant tenant, LocalDateTime dateTime, ArrayList<FeedBack> comments, boolean approved){
        this.house = house;
        this.task = task;
        this.tenant = tenant;
        this.timestamp = dateTime;
        this.comments = comments;
        this.approved = approved;

    }
}

