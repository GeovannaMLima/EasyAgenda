package Agenda.res.infra;

import Agenda.res.exception.AgendamentoConflitoException;
import Agenda.res.exception.ServiceNotFoundException;
import Agenda.res.exception.UserAlreadyExistsException;
import Agenda.res.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AgendamentoConflitoException.class)
    private ResponseEntity<RestErrorMessage> handleAgendamentoConflito(AgendamentoConflitoException e, HttpServletRequest request){
        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    private ResponseEntity<RestErrorMessage> handleServiceNotFound(ServiceNotFoundException e, HttpServletRequest request){
        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> handleUserNotFound(UserNotFoundException e, HttpServletRequest request){
        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> handleAUserAlreadyExists(UserAlreadyExistsException e, HttpServletRequest request){
        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

}
