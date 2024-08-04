package hexlet.code.domain.label.mapper;

import hexlet.code.domain.label.dto.LabelCreateDTO;
import hexlet.code.domain.label.dto.LabelUpdateDTO;
import hexlet.code.mapper.JsonNullableMapper;
import hexlet.code.mapper.ReferenceMapper;
import hexlet.code.domain.label.model.Label;
import hexlet.code.domain.label.dto.LabelDTO;
import hexlet.code.domain.label.repository.LabelRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {

    @Autowired
    private LabelRepository labelRepository;

    public abstract Label map(LabelCreateDTO dto);
    public abstract LabelDTO map(Label model);
    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);

    public List<Long> toIds(List<Label> labels) {
        return labels.stream().map(Label::getId).toList();
    }

    public List<Label> map(List<Long> ids) {
        return labelRepository.findAllById(ids);
    }

}
