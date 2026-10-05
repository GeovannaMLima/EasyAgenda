package Agenda.res.Controllers;

import Agenda.res.Dtos.ProfissionalRequestDto;
import Agenda.res.Dtos.ProfissionalResponseDto;
import Agenda.res.Services.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Provider;

@RestController
@RequestMapping("/profissional")
public class ProfissionalController {
    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @PostMapping("/register")
    public ResponseEntity<ProfissionalResponseDto> registerProfissional(@Valid @RequestBody ProfissionalRequestDto requestdto){
        var response= profissionalService.saveProfissional(requestdto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
