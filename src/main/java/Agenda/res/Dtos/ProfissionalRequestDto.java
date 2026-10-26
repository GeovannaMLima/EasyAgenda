package Agenda.res.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ProfissionalRequestDto(@NotEmpty String nome,
                                     @NotEmpty String email,
                                     @NotEmpty @Size(min =8 ) String senha) {
}
