package Agenda.res.Dtos;

import Agenda.res.models.Profissional;

import java.math.BigDecimal;

public record ServicoResponseDto(Long id,
                                 String nome,
                                 Integer duracao,
                                 BigDecimal price,
                                 String nomeProfissional) {
}
