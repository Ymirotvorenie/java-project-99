package hexlet.code.domain.task.service;

import hexlet.code.domain.task.dto.TaskCreateDTO;
import hexlet.code.domain.task.dto.TaskDTO;
import hexlet.code.domain.task.dto.TaskParamsDTO;
import hexlet.code.domain.task.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.domain.task.mapper.TaskMapper;
import hexlet.code.domain.task.repository.TaskRepository;
import hexlet.code.domain.user.repository.UserRepository;
import hexlet.code.domain.task.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskSpecification specBuilder;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);

        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();

    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO update(Long id, TaskUpdateDTO taskData) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskData, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void destroy(Long id) {
        taskRepository.deleteById(id);
    }
}
