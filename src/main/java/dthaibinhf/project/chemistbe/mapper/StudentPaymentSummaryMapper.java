package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.StudentPaymentSummaryDTO;
import dthaibinhf.project.chemistbe.model.StudentPaymentSummary;
import org.mapstruct.*;

/**
 * MapStruct mapper for StudentPaymentSummary entity and DTO conversions.
 * 
 * This mapper handles the conversion between StudentPaymentSummary entities and DTOs,
 * including complex mappings for related entities and calculated fields.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentPaymentSummaryMapper {
    
    /**
     * Convert StudentPaymentSummary entity to DTO.
     * 
     * @param studentPaymentSummary the entity to convert
     * @return the corresponding DTO
     */
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "fee.id", target = "feeId")
    @Mapping(source = "fee.name", target = "feeName")
    @Mapping(source = "academicYear.id", target = "academicYearId")
    @Mapping(source = "academicYear.year", target = "academicYearName")
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    @Mapping(source = "totalAmountDue", target = "totalAmountDue")
    @Mapping(source = "totalAmountPaid", target = "totalAmountPaid")
    @Mapping(source = "outstandingAmount", target = "outstandingAmount")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "enrollmentDate", target = "enrollmentDate")
    @Mapping(target = "completionRate", expression = "java(studentPaymentSummary.getPaymentCompletionRate())")
    @Mapping(target = "isOverdue", expression = "java(studentPaymentSummary.isOverdue())")
    @Mapping(target = "isFullyPaid", expression = "java(studentPaymentSummary.isFullyPaid())")
    StudentPaymentSummaryDTO toDto(StudentPaymentSummary studentPaymentSummary);
    
    /**
     * Convert StudentPaymentSummaryDTO to entity.
     * Note: This method is primarily for creation scenarios and doesn't handle
     * all relationships that need to be resolved from the database.
     * 
     * @param studentPaymentSummaryDTO the DTO to convert
     * @return the corresponding entity
     */
    @Mapping(target = "student", ignore = true) // Will be set by the service layer
    @Mapping(target = "fee", ignore = true) // Will be set by the service layer
    @Mapping(target = "academicYear", ignore = true) // Will be set by the service layer
    @Mapping(target = "group", ignore = true) // Will be set by the service layer
    StudentPaymentSummary toEntity(StudentPaymentSummaryDTO studentPaymentSummaryDTO);
    
    /**
     * Partially update an existing StudentPaymentSummary entity from DTO.
     * This method updates only the fields that are not null in the DTO.
     * 
     * @param studentPaymentSummaryDTO the DTO with updated values
     * @param studentPaymentSummary the existing entity to update
     * @return the updated entity
     */
    @Mapping(target = "student", ignore = true) // Don't change relationships
    @Mapping(target = "fee", ignore = true) // Don't change relationships
    @Mapping(target = "academicYear", ignore = true) // Don't change relationships
    @Mapping(target = "group", ignore = true) // Don't change relationships
    @Mapping(target = "id", ignore = true) // Never update ID
    @Mapping(target = "createdAt", ignore = true) // Never update creation time
    @Mapping(target = "updatedAt", ignore = true) // Will be auto-updated
    @Mapping(target = "endAt", ignore = true) // Managed by service layer
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    StudentPaymentSummary partialUpdate(StudentPaymentSummaryDTO studentPaymentSummaryDTO, 
                                       @MappingTarget StudentPaymentSummary studentPaymentSummary);
}