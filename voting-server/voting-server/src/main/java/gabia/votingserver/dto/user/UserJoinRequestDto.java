package gabia.votingserver.dto.user;

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
