package Agenda.res.Dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequestDto(@NotNull Long clienteId,
                                    @NotNull Long profissionalId,
                                    @NotNull Long servicoId,
                                    @NotNull @Future LocalDateTime dataHoraInicio) {
}
