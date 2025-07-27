package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.TeacherMonthlySummaryDTO;
import dthaibinhf.project.chemistbe.model.TeacherMonthlySummary;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between TeacherMonthlySummary entity and TeacherMonthlySummaryDTO.
 * This interface defines the mapping rules for transforming teacher monthly summary data
 * between the entity layer and the DTO layer.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, 
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeacherMonthlySummaryMapper {
    
    /**
     * Convert TeacherMonthlySummaryDTO to TeacherMonthlySummary entity.
     * 
     * @param dto The DTO to convert
     * @return The corresponding entity
     */
    @Mapping(target = "teacher", ignore = true) // Teacher will be set in service layer
    TeacherMonthlySummary toEntity(TeacherMonthlySummaryDTO dto);

    /**
     * Convert TeacherMonthlySummary entity to TeacherMonthlySummaryDTO.
     * 
     * @param entity The entity to convert
     * @return The corresponding DTO
     */
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.account.name", target = "teacherName")
    TeacherMonthlySummaryDTO toDto(TeacherMonthlySummary entity);

    /**
     * Partially update TeacherMonthlySummary entity with data from DTO.
     * Null values in the DTO will not overwrite existing entity values.
     * 
     * @param dto The DTO with update data
     * @param entity The entity to update
     * @return The updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "teacher", ignore = true) // Teacher relationship should not be updated
    TeacherMonthlySummary partialUpdate(TeacherMonthlySummaryDTO dto, @MappingTarget TeacherMonthlySummary entity);

}