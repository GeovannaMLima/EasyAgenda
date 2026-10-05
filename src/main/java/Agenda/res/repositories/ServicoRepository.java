package Agenda.res.repositories;

import Agenda.res.models.Servico;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico,Long> {
    Optional<Servico> findById(@NotNull Long id);
}
