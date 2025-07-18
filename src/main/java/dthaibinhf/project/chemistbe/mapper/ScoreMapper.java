package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.ScoreDTO;
import dthaibinhf.project.chemistbe.model.Score;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface ScoreMapper {
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "student", ignore = true)
    Score toEntity(ScoreDTO scoreDTO);

    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "exam.name", target = "examName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    ScoreDTO toDto(Score score);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Score partialUpdate(ScoreDTO scoreDTO, @MappingTarget Score score);
}