package Agenda.res.Dtos;

import java.util.List;

public record ProfissionalResponseDto(Long id,
                                      String nome,
                                      String email,
                                      List<String> nomesServico) {
}
