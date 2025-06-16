package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "exam")
public class Exam extends BaseEntity {
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "test_date", nullable = false)
    private OffsetDateTime testDate;

    @OneToMany(mappedBy = "exam")
    @JsonManagedReference
    private Set<Score> scores = new LinkedHashSet<>();

}