package Agenda.res.Services;

import Agenda.res.Dtos.ProfissionalRequestDto;
import Agenda.res.Dtos.ProfissionalResponseDto;
import Agenda.res.exception.UserAlreadyExistsException;
import Agenda.res.exception.UserNotFoundException;
import Agenda.res.models.Profissional;
import Agenda.res.models.Servico;
import Agenda.res.repositories.ProfissionalRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    public ProfissionalResponseDto saveProfissional(ProfissionalRequestDto request){
        if(profissionalRepository.findByEmail(request.email()).isPresent()){
            throw new UserAlreadyExistsException("Profissional ja registrado");
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

    //listar tudo
    public List<ProfissionalResponseDto> listAll(ProfissionalRequestDto request){
        List<Profissional>profissionalModel = profissionalRepository.findAll();
        List<ProfissionalResponseDto> response = new ArrayList<>();
        for(Profissional profissional: profissionalModel){
            List<String> nomesServicos= profissional.getServicos()==null
                    ? List.of()
                    :profissional.getServicos().stream()
                    .map(Servico::getNome)
                    .toList();

            response.add(new ProfissionalResponseDto(profissional.getId(),
                    profissional.getNome(),
                    profissional.getEmail(),
                    nomesServicos));
        }
        return response;
    }
    //listar por id
    public ProfissionalResponseDto listById(Long id){
        var profissional = profissionalRepository.findById(id).orElseThrow(()-> new UserNotFoundException("cliente não encontrado"));
        List<String> nomesServicos= profissional.getServicos()==null
                ? List.of()
                :profissional.getServicos().stream()
                .map(Servico::getNome)
                .toList();
        return new ProfissionalResponseDto(profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                nomesServicos);
    }
}
