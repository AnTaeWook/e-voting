package gabia.votingserver.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank String userId,
        @NotBlank String password
) {
}
