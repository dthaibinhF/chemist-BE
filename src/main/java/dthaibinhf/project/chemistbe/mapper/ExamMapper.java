package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.ExamDTO;
import dthaibinhf.project.chemistbe.model.Exam;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)public interface ExamMapper {
    Exam toEntity(ExamDTO examDTO);

    @AfterMapping
    default void linkScores(@MappingTarget Exam exam) {
        exam.getScores().forEach(score -> score.setExam(exam));
    }

    ExamDTO toDto(Exam exam);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)Exam partialUpdate(ExamDTO examDTO, @MappingTarget Exam exam);
}