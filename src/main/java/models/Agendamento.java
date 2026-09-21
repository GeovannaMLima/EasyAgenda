package models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="agendamento")
public class Agendamento {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name="profissional_id")
    private Profissional profissional;

    @ManyToOne
    @JoinColumn(name="servico_id")
    private Servico servico;

    private LocalDateTime dataHoraInicio;




}
