package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "school")
public class School extends BaseEntity {
    @Column(name = "name", nullable = false, length = 50)
    private String name;

}