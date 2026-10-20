package Agenda.res.Controllers;

import Agenda.res.Dtos.AgendamentoRequestDto;
import Agenda.res.Dtos.AgendamentoResponseDto;
import Agenda.res.Services.AgendamentoService;
import Agenda.res.models.Agendamento;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static javax.management.Query.value;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping("/add")
    public ResponseEntity<AgendamentoResponseDto> createAgendamento(@Valid @RequestBody AgendamentoRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.createAgendamento(request));
    }

    @GetMapping("/{id}/list_profissional")
    public ResponseEntity<Page<AgendamentoResponseDto>> getListByProfissional(@PathVariable(value="id")Long id, @PageableDefault(page=0,size=10,sort="dataHoraInicio",direction= Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(agendamentoService.getAgendamentoByProfissional(id,pageable));
    }

    @GetMapping("/{id}/list_cliente")
    public ResponseEntity<Page<AgendamentoResponseDto>> getListByCliente(@PathVariable(value="id") Long id, @PageableDefault(page=0,size=10,sort="dataHoraInicio",direction= Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(agendamentoService.getAgendamentoByCliente(id, pageable));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelarAgendamento(@PathVariable(value="id") Long id){
        agendamentoService.cancelarAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}
