package hexlet.code.domain.label.controller;

import hexlet.code.domain.label.dto.LabelCreateDTO;
import hexlet.code.domain.label.dto.LabelDTO;
import hexlet.code.domain.label.dto.LabelUpdateDTO;
import hexlet.code.domain.label.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        return labelService.create(labelData);
    }

    @GetMapping(path = "/{id}")
    public LabelDTO show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PutMapping(path = "/{id}")
    public LabelDTO update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO labelData) {
        return labelService.update(id, labelData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelService.destroy(id);
    }
}
