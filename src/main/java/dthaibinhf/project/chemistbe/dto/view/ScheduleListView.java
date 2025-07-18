package dthaibinhf.project.chemistbe.dto.view;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;
import dthaibinhf.project.chemistbe.model.Schedule;

import java.time.OffsetDateTime;

@EntityView(Schedule.class)
public interface ScheduleListView {
    Integer getId();
    @Mapping("group.id")
    Integer getGroupId();
    @Mapping("group.name")
    String getGroupName();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    String getDeliveryMode();
    @Mapping("room.id")
    Integer getRoomId();
    @Mapping("room.name")
    String getRoomName();
    @Mapping("teacher.id")
    Integer getTeacherId();
    @Mapping("teacher.account.name")
    String getTeacherName();
}