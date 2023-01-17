package gabia.votingserver.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    EMPTY_CLAIM(HttpStatus.UNAUTHORIZED, "No authentication information in claim"),
    DUPLICATED_ID(HttpStatus.CONFLICT, "ID already exists"),
    WRONG_PERIOD(HttpStatus.BAD_REQUEST, "Not voting period"),
    EXCEED_VOTE(HttpStatus.BAD_REQUEST, "Exceeding the number of possible votes"),
    DUPLICATED_VOTE(HttpStatus.BAD_REQUEST, "Duplicate voting is not allowed"),
    NO_AUTHENTICATION(HttpStatus.FORBIDDEN, "Cannot find authentication in token"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
