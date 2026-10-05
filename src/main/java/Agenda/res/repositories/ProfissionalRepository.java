package Agenda.res.repositories;

import Agenda.res.models.Cliente;
import Agenda.res.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfissionalRepository extends JpaRepository<Profissional,Long> {
    Optional<Profissional> findByEmail(String email);

    Optional<Profissional>findById(Long id);
}
