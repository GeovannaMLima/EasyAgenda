package Agenda.res.Dtos;

import Agenda.res.models.Profissional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoRequestDto( @NotEmpty String nome,
                                @NotNull Integer duracao,
                                @NotNull BigDecimal price,
                                @NotNull Long profissionalId) {
}
