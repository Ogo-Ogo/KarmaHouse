package com.masa.karma_house.entities;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", indexes = {@Index(columnList = "date_time")})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FeedBack {
    public static final int START_SEQ = 1;

    @Id
    @SequenceGenerator(name = "feedback_seq", sequenceName = "feedback_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_seq")
    @Setter(value=AccessLevel.NONE)
    private long id;

    @JoinColumn(name = "task_log")
    @ManyToOne(fetch = FetchType.LAZY)
    private TaskLog task_log;

    @JoinColumn(name = "tenant_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    private CommentTemplate comment;

    @Column(name = "date_time", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp;

    public FeedBack(TaskLog taskLog, Tenant tenant, CommentTemplate comment, LocalDateTime dateTime) {
        this.task_log = taskLog;
        this.tenant = tenant;
        this.comment = comment;
        this.timestamp = dateTime;
    }
}
