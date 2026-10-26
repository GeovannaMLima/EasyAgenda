package Agenda.res.Services;

import Agenda.res.Dtos.ClienteRequestDto;
import Agenda.res.Dtos.ClienteResponseDto;
import Agenda.res.SecurityAuth.service.UserService;
import Agenda.res.exception.UserAlreadyExistsException;
import Agenda.res.exception.UserNotFoundException;
import Agenda.res.models.Cliente;
import Agenda.res.models.TipoDeUser;
import Agenda.res.repositories.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final UserService userService;

    public ClienteService(ClienteRepository clienteRepository,UserService userService) {
        this.clienteRepository = clienteRepository;
        this.userService = userService;
    }

    //criar cliente
    @Transactional
    public ClienteResponseDto createCliente(ClienteRequestDto request){
        if(clienteRepository.findByEmail(request.email()).isPresent()){
            throw new UserAlreadyExistsException("Usuario ja Existe");
        }
        var user= userService.criarUsuario(request.nome(),request.email(),request.senha(), TipoDeUser.CLIENTE);
        var cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setUser(user);

        clienteRepository.save(cliente);

        return new ClienteResponseDto(cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone());
    }
    // lista todos
    public List<ClienteResponseDto> listAllClientes(){
        List<Cliente> clienteModel= clienteRepository.findAll();
        List<ClienteResponseDto> response = new ArrayList<>();
        for(Cliente cliente:clienteModel){
            response.add(new ClienteResponseDto(cliente.getId(),
                    cliente.getNome(),
                    cliente.getEmail(),
                    cliente.getTelefone()));
        }
        return response;
    }
    //listar por id
    public ClienteResponseDto listById(Long id){
        var cliente = clienteRepository.findById(id).orElseThrow(()-> new UserNotFoundException("cliente não encontrado"));
        return new ClienteResponseDto(cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone());
    }
}
