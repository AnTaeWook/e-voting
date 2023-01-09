package gabia.votingserver.dto;

import lombok.Data;

@Data
public class UserLoginRequestDto {

    private String userId;
    private String password;
}
