package Agenda.res.repositories;

import Agenda.res.models.Cliente;
import Agenda.res.models.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente,Long> {
    Optional<Cliente> findByEmail(String email);
    boolean existsById(Long id);
    Optional<Cliente>findById(Long id);
}
