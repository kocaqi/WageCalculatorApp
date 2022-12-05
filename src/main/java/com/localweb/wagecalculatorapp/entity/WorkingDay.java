package com.localweb.wagecalculatorapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDate;

@Entity
@Table(name = "working_days")
@Data
public class WorkingDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    @LazyCollection(LazyCollectionOption.FALSE)
    private User user;
    @Column(name = "hours", nullable = false)
    private int hours;
}
