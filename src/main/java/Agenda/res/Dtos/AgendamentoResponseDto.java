package Agenda.res.Dtos;

import java.time.LocalDateTime;

public record AgendamentoResponseDto(Long id,
                                     String nomeCliente,
                                     String nomeProfissional,
                                     String nomeServico,
                                     LocalDateTime dataHoraInicio,
                                     LocalDateTime dataHoraFim) {
}
