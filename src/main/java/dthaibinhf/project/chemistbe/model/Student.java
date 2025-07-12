package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student extends BaseEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "parent_phone", length = 15)
    private String parentPhone;

    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private Set<Score> scores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private Set<Attendance> attendances = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private Set<PaymentDetail> paymentDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<StudentDetail> studentDetails = new LinkedHashSet<>();

}