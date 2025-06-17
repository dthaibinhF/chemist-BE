package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.ScoreDTO;
import dthaibinhf.project.chemistbe.model.Score;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScoreMapper {

    /**
     * TODO: have to manually add in service
     */
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