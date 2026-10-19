package Agenda.res.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Este usuário ja foi cadastrado no sistema");
    }
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
