package com.br.joaquim.agendamentoBarbearia.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "servico")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private Integer duracao;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    private Set<AgendamentoEntity> agendamento = new HashSet<>();
}
