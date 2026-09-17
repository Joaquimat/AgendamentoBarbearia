package com.br.joaquim.agendamentoBarbearia.Dto;

import com.br.joaquim.agendamentoBarbearia.Entity.ServicoEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AgendamentoDto {

    private Integer Id;
    @NotNull
    private Integer servicoId;
    @NotNull
    private Integer usuarioId;
    @NotNull
    private LocalDateTime dataInicio;
    @NotNull
    private LocalDateTime dataFim;
}
