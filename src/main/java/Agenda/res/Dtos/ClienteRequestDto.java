package Agenda.res.Dtos;

import jakarta.validation.constraints.NotEmpty;

public record ClienteRequestDto(@NotEmpty String nome,
                                @NotEmpty String email,
                                @NotEmpty String telefone) {
}
