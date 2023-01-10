package gabia.votingserver.controller;

import gabia.votingserver.dto.user.TokenInfo;
import gabia.votingserver.dto.user.UserJoinRequestDto;
import gabia.votingserver.dto.user.UserJoinResponseDto;
import gabia.votingserver.dto.user.UserLoginRequestDto;
import gabia.votingserver.repository.UserRepository;
import gabia.votingserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/sign-up")
    public UserJoinResponseDto join(@RequestBody UserJoinRequestDto userJoinRequestDto) {
        return userService.create(userJoinRequestDto);
    }

    @PostMapping("/login")
    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String userId = userLoginRequestDto.getUserId();
        String password = userLoginRequestDto.getPassword();
        return userService.login(userId, password);
    }
}
