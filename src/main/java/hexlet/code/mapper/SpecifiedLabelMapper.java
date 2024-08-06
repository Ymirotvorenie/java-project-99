package hexlet.code.mapper;

import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class, LabelMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public class SpecifiedLabelMapper {
    @Autowired
    private LabelRepository labelRepository;

    public Set<Long> labelsToIds(Set<Label> label) {
        return label.stream().map(Label::getId).collect(Collectors.toSet());
    }

    public Set<Label> idsToLabels(Set<Long> ids) {
        return ids.stream().map(id -> labelRepository.findById(id).orElseThrow()).collect(Collectors.toSet());
    }
}
