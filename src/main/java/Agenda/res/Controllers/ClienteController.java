package Agenda.res.Controllers;

import Agenda.res.Dtos.ClienteRequestDto;
import Agenda.res.Dtos.ClienteResponseDto;
import Agenda.res.Services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/register")
    public ResponseEntity<ClienteResponseDto>registerCliente(@Valid @RequestBody ClienteRequestDto requestDto){
        var response = clienteService.createCliente(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
