package Agenda.res.repositories;

import Agenda.res.models.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento,Long> {
    //buscar por profissional id, e datahorainicio entre duas datas
    List<Agendamento> findByProfissionalIdAndDataHoraInicioBetween(
            Long profissionalId, LocalDateTime inicio, LocalDateTime fim
    );
    Page<Agendamento> findByProfissionalId(Long profissionalId, Pageable pageable);
    Page<Agendamento> findByClienteId(Long clienteId, Pageable pageable);

}