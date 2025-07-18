package dthaibinhf.project.chemistbe.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"group\"")
public class Group extends BaseEntity {
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "level", nullable = false, length = 10)
    private String level;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonBackReference
    private Fee fee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @JsonBackReference
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grade_id", nullable = false)
    @JsonBackReference
    private Grade grade;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<GroupSchedule> groupSchedules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "group")
    @JsonManagedReference
    private Set<Schedule> schedules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "group")
    @JsonManagedReference
    private Set<StudentDetail> studentDetails = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "group_session_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "group_session_id"))
    @JsonManagedReference
    private Set<GroupSession> groupSessions = new LinkedHashSet<>();

}