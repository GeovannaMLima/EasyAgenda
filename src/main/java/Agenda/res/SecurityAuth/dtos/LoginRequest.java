package Agenda.res.SecurityAuth.dtos;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty String email,@NotEmpty String senha) {
}
