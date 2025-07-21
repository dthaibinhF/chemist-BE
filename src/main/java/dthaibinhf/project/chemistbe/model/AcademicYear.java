package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "academic_year")
public class AcademicYear extends BaseEntity {
    @Column(name = "\"year\"", nullable = false, length = 9)
    private String year;

}