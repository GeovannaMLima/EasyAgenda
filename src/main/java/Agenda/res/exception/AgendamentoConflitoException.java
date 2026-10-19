package Agenda.res.exception;

public class AgendamentoConflitoException extends RuntimeException {
    public AgendamentoConflitoException(){
        super("O profissional ja possui agendamento para esse Horário");
    }
    public AgendamentoConflitoException(String message) {
        super(message);
    }
}
