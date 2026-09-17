package com.br.joaquim.agendamentoBarbearia.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ServicoDto {

    @NotNull
    private String nome;

    @NotNull
    private Integer duracao;

    @NotNull
    private Double valor;
}
