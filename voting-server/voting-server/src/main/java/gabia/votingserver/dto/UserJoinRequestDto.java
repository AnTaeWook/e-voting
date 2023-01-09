package gabia.votingserver.dto;

import gabia.votingserver.domain.Role;
import lombok.Data;

@Data
public class UserJoinRequestDto {

    private String userId;
    private String password;
    private String name;
    private Role role;
    private Integer voteRights;
}
