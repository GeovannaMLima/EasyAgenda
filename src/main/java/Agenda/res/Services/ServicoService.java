package Agenda.res.Services;

import Agenda.res.Dtos.ServicoRequestDto;
import Agenda.res.Dtos.ServicoResponseDto;
import Agenda.res.models.Profissional;
import Agenda.res.models.Servico;
import Agenda.res.repositories.ProfissionalRepository;
import Agenda.res.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ProfissionalRepository profissionalRepository;

    public ServicoService(ServicoRepository servicoRepository, ProfissionalRepository profissionalRepository) {
        this.servicoRepository = servicoRepository;
        this.profissionalRepository = profissionalRepository;

    }

    public ServicoResponseDto add(ServicoRequestDto request){
        Profissional profissional = profissionalRepository.findById(request.profissionalId()).orElseThrow(()-> new RuntimeException("Prfissional não encontrado"));

        var servico = new Servico(request.nome(),request.duracao(),request.price(), profissional);

        servicoRepository.save(servico);

        return new ServicoResponseDto(servico.getId(),
                servico.getNome(),
                servico.getDuracaoMinutos(),
                servico.getPrice(),
                servico.getProfissional().getNome());

    }
}
