package Agenda.res.Dtos;

import jakarta.validation.constraints.NotEmpty;

public record ProfissionalRequestDto(@NotEmpty String nome,
                                     @NotEmpty String email) {
}
