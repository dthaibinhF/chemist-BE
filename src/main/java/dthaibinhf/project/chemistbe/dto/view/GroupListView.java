package dthaibinhf.project.chemistbe.dto.view;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;
import dthaibinhf.project.chemistbe.model.Group;

@EntityView(Group.class)
public interface GroupListView {
    Integer getId();
    String getName();
    String getLevel();
    @Mapping("fee.id")
    Integer getFeeId();
    @Mapping("fee.name")
    String getFeeName();
    @Mapping("academicYear.id")
    Integer getAcademicYearId();
    @Mapping("academicYear.year")
    String getAcademicYear();
    @Mapping("grade.id")
    Integer getGradeId();
    @Mapping("grade.name")
    String getGradeName();
}