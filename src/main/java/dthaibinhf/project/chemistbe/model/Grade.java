package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "grade")
public class Grade extends BaseEntity {
    @Column(name = "name", nullable = false, length = 10)
    private String name;

}