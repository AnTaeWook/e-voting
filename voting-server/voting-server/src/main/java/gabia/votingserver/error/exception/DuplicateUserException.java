package gabia.votingserver.error.exception;

import gabia.votingserver.error.code.ErrorCode;

public class DuplicateUserException extends RestApiException {

    public DuplicateUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
