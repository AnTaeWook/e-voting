package gabia.votingserver.dto.user;

import gabia.votingserver.domain.type.Role;
import gabia.votingserver.domain.User;

public record UserJoinResponseDto (String userId, String name, Integer voteRights, Role role) {

    public static UserJoinResponseDto from(User user) {
        return new UserJoinResponseDto(
                user.getUserId(),
                user.getName(),
                user.getVoteRights(),
                user.getRole()
        );
    }
}
