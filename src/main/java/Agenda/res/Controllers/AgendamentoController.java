package Agenda.res.Controllers;

import Agenda.res.Dtos.AgendamentoRequestDto;
import Agenda.res.Dtos.AgendamentoResponseDto;
import Agenda.res.Services.AgendamentoService;
import Agenda.res.models.Agendamento;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping("/add")
    public ResponseEntity<AgendamentoResponseDto> createAgendamento(@Valid @RequestBody AgendamentoRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.createAgendamento(request));
    }
}
