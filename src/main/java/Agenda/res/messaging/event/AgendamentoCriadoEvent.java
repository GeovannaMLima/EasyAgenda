package Agenda.res.messaging.event;

import java.time.LocalDateTime;
//info do evento q acnteceu
public record AgendamentoCriadoEvent(Long agendamentoId,
                                     String nomeCliente,
                                     String nomeProfissional,
                                     LocalDateTime dataHoraInicio) {
}
