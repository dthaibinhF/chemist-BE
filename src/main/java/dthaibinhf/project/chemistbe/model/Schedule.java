package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Model class representing a schedule entity in the system.
 * A schedule defines when a group meets, including start and end times, delivery mode, and location.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedule")
public class Schedule extends BaseEntity {
    /**
     * The group associated with this schedule.
     * Many schedules can belong to one group.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;

    /**
     * The start time of the schedule.
     * Required field.
     */
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    /**
     * The end time of the schedule.
     * Required field.
     */
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    /**
     * The mode of delivery for this schedule (e.g., "online", "in-person").
     * Required field with a maximum length of 20 characters.
     */
    @Column(name = "delivery_mode", nullable = false, length = 20)
    private String deliveryMode;

    /**
     * The meeting link for online sessions.
     * Optional field.
     */
    @Column(name = "meeting_link", length = Integer.MAX_VALUE)
    private String meetingLink;

    /**
     * The teacher assigned to this schedule.
     * Many schedules can be assigned to one teacher.
     * Optional field.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonBackReference
    private Teacher teacher;

    /**
     * The room where this schedule takes place.
     * Many schedules can be assigned to one room.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    /**
     * The set of attendance records associated with this schedule.
     * One schedule can have many attendance records.
     */
    @OneToMany(mappedBy = "schedule")
    @JsonManagedReference
    @Builder.Default
    private Set<Attendance> attendances = new LinkedHashSet<>();

}
