package hexlet.code.mapper;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class, LabelMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public class SpecifiedTaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public TaskStatus getTaskStatusByStatus(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow();
    }

}
