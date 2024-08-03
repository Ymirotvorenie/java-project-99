package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new UserCreateDTO();
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        var user = userMapper.map(userData);
        userRepository.save(user);

        taskStatusRepository.saveAll(seedTaskStatuses());

        var task = new Task();
        task.setAssignee(user);
        task.setName("Test task name");
        task.setDescription("Test task description");
        task.setTaskStatus(taskStatusRepository.findById(1L).get());
        task.setIndex(33);
        taskRepository.save(task);

        labelRepository.saveAll(seedLabels());

    }

    private List<TaskStatus> seedTaskStatuses() {
        var taskStatuses = new ArrayList<TaskStatus>();

        taskStatuses.add(new TaskStatus("Draft", "draft"));
        taskStatuses.add(new TaskStatus("ToReview", "to_review"));
        taskStatuses.add(new TaskStatus("ToBeFixed", "to_be_fixed"));
        taskStatuses.add(new TaskStatus("ToPublish", "to_publish"));
        taskStatuses.add(new TaskStatus("Published", "published"));

        return taskStatuses;
    }

    private List<Label> seedLabels() {
        var labels = new ArrayList<Label>();

        labels.add(new Label("feature"));
        labels.add(new Label("bug"));

        return labels;
    }
}
