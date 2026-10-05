package Agenda.res.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="servicos")
public class Servico implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer duracaoMinutos;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name ="profissional_id")
    private Profissional profissional;


    public Servico(String nome, Integer duracaoMinutos, BigDecimal price, Profissional profissional) {
        this.nome = nome;
        this.duracaoMinutos = duracaoMinutos;
        this.price = price;
        this.profissional = profissional;
    }
}
