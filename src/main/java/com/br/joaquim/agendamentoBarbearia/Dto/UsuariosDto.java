package com.br.joaquim.agendamentoBarbearia.Dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UsuariosDto {

    @NotNull
    private String nome;

    @NotNull
    private String email;

}
