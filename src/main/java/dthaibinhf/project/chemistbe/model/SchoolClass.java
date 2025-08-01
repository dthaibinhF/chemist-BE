package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school_class")
public class SchoolClass extends BaseEntity {
    @Column(name = "name", nullable = false, length = 20)
    private String name;

}