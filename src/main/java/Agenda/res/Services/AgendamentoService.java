package Agenda.res.Services;

import Agenda.res.Dtos.AgendamentoRequestDto;
import Agenda.res.Dtos.AgendamentoResponseDto;
import Agenda.res.messaging.event.AgendamentoCriadoEvent;
import Agenda.res.messaging.publisher.AgendamentoEventPublisher;
import Agenda.res.models.Agendamento;
import Agenda.res.models.Cliente;
import Agenda.res.models.Profissional;
import Agenda.res.models.Servico;
import Agenda.res.repositories.AgendamentoRepository;
import Agenda.res.repositories.ClienteRepository;
import Agenda.res.repositories.ProfissionalRepository;
import Agenda.res.repositories.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {
    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProfissionalRepository profissionalRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    //injeta o publisher
    private final AgendamentoEventPublisher eventPublisher;

    public AgendamentoService(AgendamentoEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public AgendamentoResponseDto createAgendamento(AgendamentoRequestDto request){
        Cliente cliente = clienteRepository.findById(request.clienteId()).orElseThrow(()->new RuntimeException("Cliente nao encontrado"));

        Profissional profissional = profissionalRepository.findById(request.profissionalId()).orElseThrow(()->new RuntimeException("Profissional não encontrado"));

        Servico servico = servicoRepository.findById(request.servicoId()).orElseThrow(()-> new RuntimeException("Servico não encontrado"));

        LocalDateTime dataInicio = request.dataHoraInicio();
        LocalDateTime dataFim= dataInicio.plusMinutes(servico.getDuracaoMinutos());

        //dia
        LocalDateTime inicioDoDia = dataInicio.toLocalDate().atStartOfDay();
        LocalDateTime fimDoDia = inicioDoDia.plusDays(1);

        List<Agendamento> agendamentosDoDia = agendamentoRepository.findByProfissionalIdAndDataHoraInicioBetween(profissional.getId(),inicioDoDia, fimDoDia);

        //percorrer os agendamento do dia e verificar comflitos
        for(Agendamento existeAgendamento : agendamentosDoDia){
            LocalDateTime existeAgendamentoInicio = existeAgendamento.getDataHoraInicio();
            LocalDateTime existeAgendamentoFim= existeAgendamentoInicio.plusMinutes(existeAgendamento.getServico().getDuracaoMinutos());

            boolean conflito = dataInicio.isBefore(existeAgendamentoFim) && existeAgendamentoInicio.isBefore(dataFim);
            if(conflito){
                throw new IllegalStateException("O profissional ja possui agendamento para esse Horário");
            }
        }
        Agendamento agendamento = new Agendamento(cliente, profissional, servico,dataInicio);
        Agendamento agendamentoSalvo= agendamentoRepository.save(agendamento);

        //instancia o evento
        AgendamentoCriadoEvent evento = new AgendamentoCriadoEvent(
                agendamentoSalvo.getId(),
                cliente.getNome(),
                profissional.getNome(),
                dataInicio
        );

        eventPublisher.publicarEventoCriado(evento);

        return new AgendamentoResponseDto(
                agendamentoSalvo.getId(),
                cliente.getNome(),
                profissional.getNome(),
                servico.getNome(),
                dataInicio,
                dataFim
        );

    }


}
