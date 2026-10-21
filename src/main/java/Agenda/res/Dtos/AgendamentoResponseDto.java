package Agenda.res.Dtos;

import Agenda.res.models.StatusAgendamento;

import java.time.LocalDateTime;

public record AgendamentoResponseDto(Long id,
                                     String nomeCliente,
                                     String nomeProfissional,
                                     String nomeServico,
                                     StatusAgendamento status,
                                     LocalDateTime dataHoraInicio,
                                     LocalDateTime dataHoraFim) {
}
