package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.ExamDTO;
import dthaibinhf.project.chemistbe.model.Exam;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ScoreMapper.class})
public interface ExamMapper {
    Exam toEntity(ExamDTO examDTO);

    @Mapping(source = "scores", target = "scores")
    ExamDTO toDto(Exam exam);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Exam partialUpdate(ExamDTO examDTO, @MappingTarget Exam exam);
}