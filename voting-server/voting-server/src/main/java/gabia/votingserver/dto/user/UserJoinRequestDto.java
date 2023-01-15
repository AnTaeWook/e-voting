package gabia.votingserver.dto.user;

import gabia.votingserver.domain.type.Role;
import jakarta.validation.constraints.NotBlank;

public record UserJoinRequestDto(
        @NotBlank String userId,
        @NotBlank String password,
        @NotBlank String name,
        Role role,
        int voteRights) {
}
