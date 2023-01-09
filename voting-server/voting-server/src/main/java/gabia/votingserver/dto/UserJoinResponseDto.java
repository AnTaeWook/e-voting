package gabia.votingserver.dto;

import gabia.votingserver.domain.Role;
import gabia.votingserver.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserJoinResponseDto {
    private String userId;
    private String name;
    private Integer voteRights;
    private Role role;

    public static UserJoinResponseDto from(User user) {
        return new UserJoinResponseDto(
                user.getUserId(),
                user.getName(),
                user.getVoteRights(),
                user.getRole()
        );
    }

    private UserJoinResponseDto(String userId, String name, Integer voteRights, Role role) {
        this.userId = userId;
        this.name = name;
        this.voteRights = voteRights;
        this.role = role;
    }
}
