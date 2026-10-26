package Agenda.res.Controllers;

import Agenda.res.Dtos.ClienteResponseDto;
import Agenda.res.Dtos.ProfissionalRequestDto;
import Agenda.res.Dtos.ProfissionalResponseDto;
import Agenda.res.Services.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Provider;
import java.util.List;

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

    @GetMapping("/{id}")
    public  ResponseEntity<ProfissionalResponseDto> getProfissionalById(@PathVariable (value="id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(profissionalService.listById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProfissionalResponseDto>> getAllProfissional(){
        return ResponseEntity.status(HttpStatus.OK).body(profissionalService.listAll());
    }
}
