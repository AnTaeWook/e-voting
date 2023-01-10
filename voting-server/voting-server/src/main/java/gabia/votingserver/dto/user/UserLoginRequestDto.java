package gabia.votingserver.dto.user;

import lombok.Data;

@Data
public class UserLoginRequestDto {

    private String userId;
    private String password;
}
