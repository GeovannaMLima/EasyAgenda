package Agenda.res.AuditoriaMessaging.event;

import java.time.LocalDateTime;

public record AgendamentoAuditoriaEvent(Long agendamentoId,
                                        AcaoAuditoria acao,
                                        Long clienteId,
                                        Long profissionalId,
                                        Long servicoId,
                                        LocalDateTime dataHoraInicio,
                                        LocalDateTime ocorridoEm) {
}
