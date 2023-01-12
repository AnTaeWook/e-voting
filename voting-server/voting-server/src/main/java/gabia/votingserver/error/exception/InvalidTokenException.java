package gabia.votingserver.error.exception;

import gabia.votingserver.error.code.ErrorCode;

public class InvalidTokenException extends RestApiException {

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
