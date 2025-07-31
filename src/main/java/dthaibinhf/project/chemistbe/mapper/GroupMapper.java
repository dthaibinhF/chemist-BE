package dthaibinhf.project.chemistbe.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.GroupListDTO;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.model.Grade;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.repository.AcademicYearRepository;
import dthaibinhf.project.chemistbe.repository.FeeRepository;
import dthaibinhf.project.chemistbe.repository.GradeRepository;

/**
 * The Mapper have already added all item like {@link Fee} {@link AcademicYear} and {@link Grade} by their ID,
 * by using @AfterMapping. Which is, when mapping, the mapper will search each item ID, and the Group object will set item
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {FeeMapper.class, AcademicYearMapper.class, GradeMapper.class,
                GroupScheduleMapper.class, ScheduleMapper.class,
                StudentDetailMapper.class, GroupSessionMapper.class})
public abstract class GroupMapper {
    @Autowired
    protected AcademicYearRepository academicYearRepository;

    @Autowired
    protected FeeRepository feeRepository;

    @Autowired
    protected GradeRepository gradeRepository;

    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "studentDetails", ignore = true)
    @Mapping(target = "groupSessions", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "grade", ignore = true)
    abstract public Group toEntity(GroupDTO groupDTO);

    @BeforeMapping
    protected void linkRelatedEntities(@MappingTarget Group group, GroupDTO groupDTO) {
        if (groupDTO.getFeeId() != null) {
            Fee fee = feeRepository.findById(groupDTO.getFeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Fee not found: " + groupDTO.getFeeId()));
            group.setFee(fee);
        }
        if (groupDTO.getAcademicYearId() != null) {
            AcademicYear academicYear = academicYearRepository.findById(groupDTO.getAcademicYearId())
                    .orElseThrow(() -> new IllegalArgumentException("AcademicYear not found: " + groupDTO.getAcademicYearId()));
            group.setAcademicYear(academicYear);
        }
        if (groupDTO.getGradeId() != null) {
            Grade grade = gradeRepository.findById(groupDTO.getGradeId())
                    .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + groupDTO.getGradeId()));
            group.setGrade(grade);
        }
        if (groupDTO.getStudentDetails() != null) {
            group.getStudentDetails().forEach(studentDetail -> studentDetail.setGroup(group));
        }
    }

    @AfterMapping
    protected void linkGroupSchedules(@MappingTarget Group group) {
        if (group.getGroupSchedules() != null) {
            group.getGroupSchedules().forEach(groupSchedule -> groupSchedule.setGroup(group));
        }
    }

    @AfterMapping
    protected void linkSchedules(@MappingTarget Group group) {
        if (group.getSchedules() != null) {
            group.getSchedules().forEach(schedule -> schedule.setGroup(group));
        }
    }

    @AfterMapping
    protected void linkStudentDetails(@MappingTarget Group group) {
        if (group.getStudentDetails() != null) {
            group.getStudentDetails().forEach(studentDetail -> studentDetail.setGroup(group));
        }
    }

    @Mapping(source = "fee.id", target = "feeId")
    @Mapping(source = "fee.name", target = "feeName")
    @Mapping(source = "academicYear.id", target = "academicYearId")
    @Mapping(source = "academicYear.year", target = "academicYear")
    @Mapping(source = "grade.id", target = "gradeId")
    @Mapping(source = "grade.name", target = "gradeName")
    abstract public GroupDTO toDto(Group group);

    @Mapping(source = "fee.id", target = "feeId")
    @Mapping(source = "fee.name", target = "feeName")
    @Mapping(source = "academicYear.id", target = "academicYearId")
    @Mapping(source = "academicYear.year", target = "academicYear")
    @Mapping(source = "grade.id", target = "gradeId")
    @Mapping(source = "grade.name", target = "gradeName")
    @Mapping(source = "groupSchedules", target = "groupSchedules")  // Add this mapping
    abstract public GroupListDTO toListDto(Group group);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "groupSchedules", ignore = true)
    abstract public Group partialUpdate(GroupDTO groupDTO, @MappingTarget Group group);

    @Mapping(source = "level", target = "level")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "endAt", target = "endAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "id", target = "id")
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "studentDetails", ignore = true)
    @Mapping(target = "groupSessions", ignore = true)
    @Mapping(target = "groupSchedules", ignore = true)
    abstract Group toEntity(GroupListDTO groupListDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "studentDetails", ignore = true)
    @Mapping(target = "groupSessions", ignore = true)
    @Mapping(target = "groupSchedules", ignore = true)
    abstract Group partialUpdate(GroupListDTO groupDto, @MappingTarget Group group);
}