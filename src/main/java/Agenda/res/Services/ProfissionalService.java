package Agenda.res.Services;

import Agenda.res.Dtos.ProfissionalRequestDto;
import Agenda.res.Dtos.ProfissionalResponseDto;
import Agenda.res.models.Profissional;
import Agenda.res.models.Servico;
import Agenda.res.repositories.ProfissionalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    public ProfissionalResponseDto saveProfissional(ProfissionalRequestDto request){
        if(profissionalRepository.findByEmail(request.email()).isPresent()){
            throw new RuntimeException("Profissional ja registrado");
        }
        var profisional= new Profissional();
        BeanUtils.copyProperties(request,profisional);

        profissionalRepository.save(profisional);

        List<String> nomesServicos= profisional.getServicos()==null
                ? List.of()
                :profisional.getServicos().stream()
                .map(Servico::getNome)
                .toList();

        return new ProfissionalResponseDto(profisional.getId(),profisional.getNome(),profisional.getEmail(),nomesServicos );
    }
}
