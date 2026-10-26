package Agenda.res.SecurityAuth.dtos;

import Agenda.res.models.User;

public record UserResponseDto(Long id,
                              String login
) {
    public UserResponseDto(User user) {
        this(user.getId(), user.getEmailUser());
    }
}