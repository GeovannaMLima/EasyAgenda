package Agenda.res.infra;

import Agenda.res.exception.AgendamentoConflitoException;
import Agenda.res.exception.ServiceNotFoundException;
import Agenda.res.exception.UserAlreadyExistsException;
import Agenda.res.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorMessage> handleGenerico(Exception e, HttpServletRequest request){
        String errorMessage = "Erro interno no servidor.";

        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                errorMessage,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RestErrorMessage> handleArgumentoInvalido(Exception e, HttpServletRequest request){
        String errorMessage = "valor inválido para o parâmetro.";

        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<RestErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request){
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error-> error.getField() + ": "+error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        RestErrorMessage threatResponse = new RestErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatResponse);
    }

}
