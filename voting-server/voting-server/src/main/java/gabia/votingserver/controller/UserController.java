package gabia.votingserver.controller;

import gabia.votingserver.dto.user.TokenInfo;
import gabia.votingserver.dto.user.UserJoinRequestDto;
import gabia.votingserver.dto.user.UserJoinResponseDto;
import gabia.votingserver.dto.user.UserLoginRequestDto;
import gabia.votingserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sign-up")
    public UserJoinResponseDto join(@RequestBody @Valid UserJoinRequestDto userJoinRequestDto) {
        return UserJoinResponseDto.from(userService.create(userJoinRequestDto));
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody @Valid UserLoginRequestDto userLoginRequestDto) {
        return userService.login(userLoginRequestDto.userId(), userLoginRequestDto.password());
    }
}
