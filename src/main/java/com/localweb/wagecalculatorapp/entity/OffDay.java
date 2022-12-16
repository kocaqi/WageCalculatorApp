package com.localweb.wagecalculatorapp.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "off_days")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OffDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OffDay offDay = (OffDay) o;
        return Objects.equals(id, offDay.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
