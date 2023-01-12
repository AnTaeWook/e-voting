package gabia.votingserver.dto.user;

import gabia.votingserver.domain.type.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserJoinRequestDto {

    private String userId;
    private String password;
    private String name;
    private Role role;
    private Integer voteRights;
}
