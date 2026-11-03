package Agenda.res.services;

import Agenda.res.AuditoriaMessaging.producer.AuditoriaProducer;
import Agenda.res.Dtos.AgendamentoRequestDto;
import Agenda.res.Dtos.AgendamentoResponseDto;
import Agenda.res.Services.AgendamentoService;
import Agenda.res.exception.AcessoNegadoException;
import Agenda.res.exception.AgendamentoConflitoException;
import Agenda.res.messaging.publisher.AgendamentoEventPublisher;
import Agenda.res.models.*;
import Agenda.res.repositories.AgendamentoRepository;
import Agenda.res.repositories.ClienteRepository;
import Agenda.res.repositories.ProfissionalRepository;
import Agenda.res.repositories.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {
    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ProfissionalRepository profissionalRepository;
    @Mock
    private ServicoRepository servicoRepository;
    @Mock
    private AgendamentoEventPublisher eventPublisher;
    @Mock
    private AuditoriaProducer auditoriaProducer;

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Test
    void deveAgendarQuandoNaoHaConflito(){
        //Arrange
        Cliente cliente= new Cliente(1L,"Ana","Ana@gmail.com","9000000000",null);
        Profissional profissional= new Profissional(1L, "Jose","Jose@gmail.com",null,null);
        Servico servico= new Servico(1L,"corte",60, new BigDecimal("50.00"), profissional );
        User userCLiente = new User();
        userCLiente.setId(1L);
        userCLiente.setTipoUser(TipoDeUser.CLIENTE);
        cliente.setUser(userCLiente);
        LocalDateTime dataInicio= LocalDateTime.now().plusDays(1);
        AgendamentoRequestDto requestDto = new AgendamentoRequestDto(1L,1L,1L, dataInicio);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        //sem nenhum agendamento pra n dar confilto list vazia
        when(agendamentoRepository.findByProfissionalIdAndDataHoraInicioBetween(any(),any(),any())).thenReturn(new ArrayList<>());
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(new Agendamento(cliente,profissional,servico,dataInicio));


        //Act
        AgendamentoResponseDto response = agendamentoService.createAgendamento(requestDto,userCLiente);


        //Assert
        assertNotNull(response);
        assertEquals("Ana", response.nomeCliente());
        verify(eventPublisher).publicarEventoCriado(any());
        verify(auditoriaProducer).publicar(any());
    }

    @Test
    void naoDeveAgendarQuandoHouverConflito(){
        //arrange
        Cliente cliente= new Cliente(1L,"Ana","Ana@gmail.com","9000000000",null);
        Profissional profissional= new Profissional(1L, "Jose","Jose@gmail.com",null,null);
        Servico servico= new Servico(1L,"corte",60, new BigDecimal("50.00"), profissional );
        User userCLiente = new User();
        userCLiente.setId(1L);
        userCLiente.setTipoUser(TipoDeUser.CLIENTE);
        cliente.setUser(userCLiente);

        LocalDateTime dataInicio= LocalDateTime.now().plusDays(1);
        AgendamentoRequestDto requestDto = new AgendamentoRequestDto(1L,1L,1L, dataInicio);

        Agendamento agendamentoExistente = new Agendamento(cliente,profissional,servico,dataInicio);


        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.findByProfissionalIdAndDataHoraInicioBetween(any(),any(),any())).thenReturn(List.of(agendamentoExistente));

        //act e assert
        //testando uma exception
        assertThrows(AgendamentoConflitoException.class, () ->{
            agendamentoService.createAgendamento(requestDto,userCLiente);
        });
        verify(agendamentoRepository,never()).save(any());
        verifyNoInteractions(eventPublisher,auditoriaProducer);
    }

    @Test
    void naoDeveListarAgendamentosDeOutroCliente(){
        Cliente cliente = new Cliente(1L,"Ana","ana@gmail.com","9000000000",null);
        User dono = new User(); dono.setId(1L); dono.setTipoUser(TipoDeUser.CLIENTE);
        cliente.setUser(dono);

        User intruso = new User(); intruso.setId(2L); intruso.setTipoUser(TipoDeUser.CLIENTE);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        assertThrows(AcessoNegadoException.class, () ->
                agendamentoService.getAgendamentoByCliente(1L, null, Pageable.unpaged(), intruso));

        verifyNoInteractions(agendamentoRepository);
    }

}
