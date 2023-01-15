package gabia.votingserver.error;

import gabia.votingserver.error.code.CommonErrorCode;
import gabia.votingserver.error.code.ErrorCode;
import gabia.votingserver.error.exception.RestApiException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        ErrorCode errorCode = CommonErrorCode.RESOURCE_NOT_FOUND;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException e) {
        ErrorCode errorCode = CommonErrorCode.NO_ELEMENT;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getHttpStatus());
    }
}
