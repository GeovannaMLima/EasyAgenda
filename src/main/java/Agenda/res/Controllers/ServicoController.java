package Agenda.res.Controllers;

import Agenda.res.Dtos.ServicoRequestDto;
import Agenda.res.Dtos.ServicoResponseDto;
import Agenda.res.Services.ServicoService;
import Agenda.res.models.Servico;
import Agenda.res.repositories.ServicoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servico")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController( ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping("/add")
    public ResponseEntity<ServicoResponseDto> create(@Valid @RequestBody ServicoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.add(request));
    }
}
