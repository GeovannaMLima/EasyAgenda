package Agenda.res.Services;

import Agenda.res.Dtos.ClienteRequestDto;
import Agenda.res.Dtos.ClienteResponseDto;
import Agenda.res.models.Cliente;
import Agenda.res.repositories.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;


    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //criar cliente
    public ClienteResponseDto createCliente(ClienteRequestDto request){
        if(clienteRepository.findByEmail(request.email()).isPresent()){
            throw new RuntimeException("Usuario ja Existe");
        }
        var cliente = new Cliente();
        BeanUtils.copyProperties(request,cliente);

        clienteRepository.save(cliente);

        return new ClienteResponseDto(cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone());
    }
}
