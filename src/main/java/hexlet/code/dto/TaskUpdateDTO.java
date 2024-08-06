package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Setter
@Getter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;

    private JsonNullable<String> status;

    private JsonNullable<Long> assigneeId;

    @Size(min = 1)
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    private JsonNullable<Set<Long>> taskLabelIds;
}
