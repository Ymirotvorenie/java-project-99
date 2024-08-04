package hexlet.code.domain.label.service;

import hexlet.code.domain.label.dto.LabelCreateDTO;
import hexlet.code.domain.label.dto.LabelDTO;
import hexlet.code.domain.label.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.domain.label.mapper.LabelMapper;
import hexlet.code.domain.label.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        return labelRepository.findAll().stream()
                .map(l -> labelMapper.map(l))
                .toList();
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        return labelMapper.map(label);
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        var label = labelMapper.map(labelData);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelData) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void destroy(Long id) {
        labelRepository.deleteById(id);
    }
}
