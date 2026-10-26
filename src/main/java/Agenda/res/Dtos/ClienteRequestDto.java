package Agenda.res.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ClienteRequestDto(@NotEmpty String nome,
                                @NotEmpty String email,
                                @NotEmpty @Size(min =8 ) String senha,
                                @NotEmpty String telefone) {
}
