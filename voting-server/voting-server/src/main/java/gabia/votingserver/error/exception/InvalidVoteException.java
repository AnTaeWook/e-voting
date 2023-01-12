package gabia.votingserver.error.exception;

import gabia.votingserver.error.code.ErrorCode;

public class InvalidVoteException extends RestApiException {

    public InvalidVoteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
