package gabia.votingserver.service;

import gabia.votingserver.domain.User;
import gabia.votingserver.dto.user.TokenInfo;
import gabia.votingserver.dto.user.UserJoinRequestDto;
import gabia.votingserver.dto.user.UserJoinResponseDto;
import gabia.votingserver.jwt.JwtTokenProvider;
import gabia.votingserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserJoinResponseDto create(UserJoinRequestDto userJoinRequestDto) {

        validateUserId(userJoinRequestDto);

        User user = User
                .builder()
                .userId(userJoinRequestDto.getUserId())
                .password(passwordEncoder.encode(userJoinRequestDto.getPassword()))
                .name(userJoinRequestDto.getName())
                .role(userJoinRequestDto.getRole())
                .voteRights(userJoinRequestDto.getVoteRights())
                .build();

        User savedUser = userRepository.save(user);

        return UserJoinResponseDto.from(user);
    }

    private void validateUserId(UserJoinRequestDto userJoinRequestDto) {
        if (userRepository.findByUserId(userJoinRequestDto.getUserId()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자 ID 입니다.");
        }
    }

    @Transactional
    public TokenInfo login(String userId, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
    }
}
