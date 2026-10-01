package com.br.joaquim.agendamentoBarbearia.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginRequestDto {


    @NotNull
    private String email;

    @NotNull
    private String senha;

}
