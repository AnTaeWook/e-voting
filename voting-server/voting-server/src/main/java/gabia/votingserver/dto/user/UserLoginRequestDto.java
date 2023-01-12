package gabia.votingserver.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
