package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room extends BaseEntity {
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "location", nullable = false, length = Integer.MAX_VALUE)
    private String location;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

}