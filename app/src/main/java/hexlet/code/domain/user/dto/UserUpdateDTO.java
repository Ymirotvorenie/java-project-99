package hexlet.code.domain.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Email
    @Column(unique = true)
    private JsonNullable<String>  email;

    private JsonNullable<String>  password;
}
