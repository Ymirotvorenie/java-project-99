package hexlet.code.domain.taskStatus.service;

import hexlet.code.domain.taskStatus.dto.TaskStatusCreateDTO;
import hexlet.code.domain.taskStatus.dto.TaskStatusDTO;
import hexlet.code.domain.taskStatus.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.domain.taskStatus.mapper.TaskStatusMapper;
import hexlet.code.domain.taskStatus.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(ts -> taskStatusMapper.map(ts))
                .toList();
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with " + id + " not found"));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusData) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with " + id + " not found"));
        taskStatusMapper.update(taskStatusData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void destroy(Long id) {
        taskStatusRepository.deleteById(id);
    }

}
