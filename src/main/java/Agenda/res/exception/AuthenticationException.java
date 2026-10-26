package Agenda.res.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(){
        super("Credenciais Inválidas");
    }
    public AuthenticationException(String message) {
        super(message);
    }
}
