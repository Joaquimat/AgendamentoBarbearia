package com.br.joaquim.agendamentoBarbearia.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CriarAgendamentoDto {
    @NonNull
    private Integer servicoId;
    @NonNull
    private LocalDateTime dataInicio;
}
