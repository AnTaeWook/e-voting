package gabia.votingserver.service;

import gabia.votingserver.domain.User;
import gabia.votingserver.dto.user.TokenInfo;
import gabia.votingserver.dto.user.UserJoinRequestDto;
import gabia.votingserver.error.code.UserErrorCode;
import gabia.votingserver.error.exception.DuplicateUserException;
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

    public User getUser(String userId) {
        return userRepository.findByUserId(userId).orElseThrow();
    }

    @Transactional
    public User create(UserJoinRequestDto userJoinRequestDto) {
        validateUserId(userJoinRequestDto);
        User user = User
                .builder()
                .userId(userJoinRequestDto.userId())
                .password(passwordEncoder.encode(userJoinRequestDto.password()))
                .name(userJoinRequestDto.name())
                .role(userJoinRequestDto.role())
                .voteRights(userJoinRequestDto.voteRights())
                .build();

        userRepository.directSave(user);
        return user;
    }

    @Transactional
    public TokenInfo login(String userId, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
    }

    private void validateUserId(UserJoinRequestDto userJoinRequestDto) {
        if (userRepository.findByUserId(userJoinRequestDto.userId()).isPresent()) {
            throw new DuplicateUserException(UserErrorCode.DUPLICATED_ID);
        }
    }
}
