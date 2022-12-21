package messenger.messenger.auth.user.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FormUserDto {

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    @Length(min = 10, max = 100, message = "password는 10자 이상입니다.")
    private String password;

    @Builder
    public FormUserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
