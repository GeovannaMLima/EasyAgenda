package Agenda.res.Services;

import Agenda.res.Dtos.ClienteRequestDto;
import Agenda.res.Dtos.ClienteResponseDto;
import Agenda.res.Dtos.ServicoRequestDto;
import Agenda.res.Dtos.ServicoResponseDto;
import Agenda.res.exception.UserNotFoundException;
import Agenda.res.models.Cliente;
import Agenda.res.models.Profissional;
import Agenda.res.models.Servico;
import Agenda.res.repositories.ProfissionalRepository;
import Agenda.res.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;

    public ServicoService(ServicoRepository servicoRepository, ProfissionalRepository profissionalRepository) {
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;

    }

    public ServicoResponseDto add(ServicoRequestDto request){
        Profissional profissional = profissionalRepository.findById(request.profissionalId()).orElseThrow(()-> new UserNotFoundException("Profissional não encontrado"));

        var servico = new Servico(request.nome(),request.duracao(),request.price(), profissional);

        servicoRepository.save(servico);

        return new ServicoResponseDto(servico.getId(),
                servico.getNome(),
                servico.getDuracaoMinutos(),
                servico.getPrice(),
                servico.getProfissional().getNome());

    }

    public List<ServicoResponseDto> listAllClientes(ServicoRequestDto request){
        List<Servico> servicoModel= servicoRepository.findAll();
        List<ServicoResponseDto> response = new ArrayList<>();
        for(Servico servico:servicoModel){
            response.add(new ServicoResponseDto(servico.getId(),
                    servico.getNome(),
                    servico.getDuracaoMinutos(),
                    servico.getPrice(),
                    servico.getProfissional().getNome()));
        }
        return response;
    }
    //listar por id
    public ServicoResponseDto listById(Long id){
        var servico = servicoRepository.findById(id).orElseThrow(()-> new UserNotFoundException("serviço não encontrado"));
        return new ServicoResponseDto(servico.getId(),
                servico.getNome(),
                servico.getDuracaoMinutos(),
                servico.getPrice(),
                servico.getProfissional().getNome());
    }
}
