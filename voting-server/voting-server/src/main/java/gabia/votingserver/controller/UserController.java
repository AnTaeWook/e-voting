package gabia.votingserver.controller;

import gabia.votingserver.domain.Role;
import gabia.votingserver.domain.User;
import gabia.votingserver.dto.TokenInfo;
import gabia.votingserver.dto.UserJoinRequestDto;
import gabia.votingserver.dto.UserJoinResponseDto;
import gabia.votingserver.dto.UserLoginRequestDto;
import gabia.votingserver.repository.UserRepository;
import gabia.votingserver.service.UserService;
import gabia.votingserver.util.SecurityUtil;
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
