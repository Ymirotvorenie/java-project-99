package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;

@Getter
public class ModelGenerator {
    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    public ModelGenerator() {
        var faker = new Faker();

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.animal().name())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore((Select.field(TaskStatus::getId)))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .toModel();
//                .ignore(Select.field(TaskStatus::getId))
//                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().characters(7, 11))
//                .supply(Select.field(TaskStatus::getSlug), () -> faker.lorem().characters(5, 11))
//                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getIndex))
                .supply(Select.field(Task::getDescription), () -> faker.lorem().characters(15, 40))
                .supply(Select.field(Task::getName), () -> faker.lorem().word())
                .ignore(Select.field(Task::getTaskStatus))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> faker.lorem().characters(5, 11))
                .ignore(Select.field(Label::getCreatedAt))
                .toModel();
    }

}
