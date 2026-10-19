package Agenda.res.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(){
        super("serviço não encontrado");
    }
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
