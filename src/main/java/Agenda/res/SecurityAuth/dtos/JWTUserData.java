package Agenda.res.SecurityAuth.dtos;

import lombok.Builder;

@Builder
public record JWTUserData(Long id, String email, String role) {
}
