package Agenda.res.infra;

import java.time.LocalDateTime;

public record RestErrorMessage(Integer code,
                               String error,
                               String message,
                               String path,
                               LocalDateTime timestamp) {
}
