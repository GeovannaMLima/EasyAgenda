package Agenda.res.Services;

import Agenda.res.AuditoriaMessaging.event.AcaoAuditoria;
import Agenda.res.AuditoriaMessaging.event.AgendamentoAuditoriaEvent;
import Agenda.res.AuditoriaMessaging.producer.AuditoriaProducer;
import Agenda.res.Dtos.AgendamentoRequestDto;
import Agenda.res.Dtos.AgendamentoResponseDto;
import Agenda.res.exception.AcessoNegadoException;
import Agenda.res.exception.AgendamentoConflitoException;
import Agenda.res.exception.ServiceNotFoundException;
import Agenda.res.exception.UserNotFoundException;
import Agenda.res.messaging.event.AgendamentoCriadoEvent;
import Agenda.res.messaging.publisher.AgendamentoEventPublisher;
import Agenda.res.models.*;
import Agenda.res.repositories.AgendamentoRepository;
import Agenda.res.repositories.ClienteRepository;
import Agenda.res.repositories.ProfissionalRepository;
import Agenda.res.repositories.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ServicoRepository servicoRepository;
    //injeta o publisher
    private final AgendamentoEventPublisher eventPublisher;

    //producer kafka
    private final AuditoriaProducer auditoriaProducer;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ClienteRepository clienteRepository,
                              ProfissionalRepository profissionalRepository,
                              ServicoRepository servicoRepository,
                              AgendamentoEventPublisher eventPublisher,
                              AuditoriaProducer auditoriaProducer) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.profissionalRepository = profissionalRepository;

        this.servicoRepository = servicoRepository;

        this.eventPublisher = eventPublisher;
        this.auditoriaProducer = auditoriaProducer;
    }

    public AgendamentoResponseDto createAgendamento(AgendamentoRequestDto request, User user){
        Cliente cliente = clienteRepository.findById(request.clienteId()).orElseThrow(()->new UserNotFoundException("Cliente nao encontrado"));
        validarAcesso(user, cliente.getUser());
        Profissional profissional = profissionalRepository.findById(request.profissionalId()).orElseThrow(()->new UserNotFoundException("Profissional não encontrado"));

        Servico servico = servicoRepository.findById(request.servicoId()).orElseThrow(()-> new ServiceNotFoundException("Servico não encontrado"));

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

            boolean conflito = dataInicio.isBefore(existeAgendamentoFim) && existeAgendamentoInicio.isBefore(dataFim) && existeAgendamento.getStatusAgendamento() == StatusAgendamento.AGENDADO;
            if(conflito){
                throw new AgendamentoConflitoException("O profissional ja possui agendamento para esse Horário");
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
        auditoriaProducer.publicar(new AgendamentoAuditoriaEvent(
                evento.agendamentoId(),
                AcaoAuditoria.CRIADO,
                agendamentoSalvo.getCliente().getId(),
                agendamentoSalvo.getProfissional().getId(),
                agendamentoSalvo.getServico().getId(),
                dataInicio,
                LocalDateTime.now()));

        return new AgendamentoResponseDto(
                agendamentoSalvo.getId(),
                cliente.getNome(),
                profissional.getNome(),
                servico.getNome(),
                agendamentoSalvo.getStatusAgendamento(),
                dataInicio,
                dataFim
        );

    }

    //listar agendamento por profissional
    public Page<AgendamentoResponseDto> getAgendamentoByProfissional(Long id,StatusAgendamento status ,Pageable pageable, User user){
        var profissional = profissionalRepository.findById(id).orElseThrow(()-> new UserNotFoundException("usuario nao encontrado"));
        validarAcesso(user,profissional.getUser());
        if(status == null){
            return agendamentoRepository.findByProfissionalId(id,pageable).map(this::dataFimToDto);
        }
        return agendamentoRepository.findByProfissionalIdAndStatusAgendamento(id,status,pageable).map(this::dataFimToDto);
    }

    //listar agendamento por cliente
    public Page<AgendamentoResponseDto> getAgendamentoByCliente(Long id, StatusAgendamento status,Pageable pageable,User user){
        var cliente = clienteRepository.findById(id).orElseThrow(()-> new UserNotFoundException("usuario nao encontrado"));

        validarAcesso(user,cliente.getUser());
        if(status == null){
            return agendamentoRepository.findByClienteId(id, pageable).map(this::dataFimToDto);
        }

        return agendamentoRepository.findByClienteIdAndStatusAgendamento(id,status, pageable).map(this::dataFimToDto);
    }

    public void cancelarAgendamento(Long id, User user){
        var agendamento = agendamentoRepository.findById(id).orElseThrow(()-> new ServiceNotFoundException("Agendamento não encontrado"));
        boolean permitido = user.getTipoUser() == TipoDeUser.ADMIN
                || souDono(user, agendamento.getCliente().getUser())
                || souDono(user, agendamento.getProfissional().getUser());

        if(!permitido){
            throw new AcessoNegadoException("Você não tem permissão para cancelar este agendamento");
        }
        if(agendamento.getStatusAgendamento() == StatusAgendamento.CANCELADO){
            throw new AgendamentoConflitoException("Agendamento ja cancelado");
        }
        agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
        auditoriaProducer.publicar(new AgendamentoAuditoriaEvent(agendamento.getId(),
                AcaoAuditoria.CANCELADO,
                agendamento.getCliente().getId(),
                agendamento.getProfissional().getId(),
                agendamento.getServico().getId(),
                agendamento.getDataHoraInicio(),
                LocalDateTime.now()));
    }

    private AgendamentoResponseDto dataFimToDto(Agendamento a){
        var servico = a.getServico();
        var datafim= a.getDataHoraInicio().plusMinutes(servico.getDuracaoMinutos());

        return new AgendamentoResponseDto(a.getId(),
                a.getCliente().getNome(),
                a.getProfissional().getNome(),
                a.getServico().getNome(),
                a.getStatusAgendamento(),
                a.getDataHoraInicio(),
                datafim);
    }

    private void validarAcesso(User logado, User dono){
        if(logado.getTipoUser() == TipoDeUser.ADMIN){
            return;
        }
        if(dono == null || !logado.getId().equals(dono.getId())){
            throw new AcessoNegadoException("Você não tem permissão para acessar este recurso");
        }
    }
    private boolean souDono(User logado, User dono){
        return dono != null && logado.getId().equals(dono.getId());
    }


}
