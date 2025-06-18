package dthaibinhf.project.chemistbe.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass // Use MappedSuperclass instead of Entity
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "create_at") // Match PostgresSQL column name
    OffsetDateTime createdAt;

    @Column(name = "update_at")
    OffsetDateTime updatedAt; //as null if the not update yet

    @Column(name = "end_at")
    OffsetDateTime endAt;

    // Lifecycle event to set createdAt and updatedAt on insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    // Lifecycle event to set updatedAt on update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    //call to soft-delete
    public void softDelete() {
        this.endAt = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }
}