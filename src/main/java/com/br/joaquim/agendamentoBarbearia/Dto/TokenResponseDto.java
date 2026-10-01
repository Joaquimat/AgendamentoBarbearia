package com.br.joaquim.agendamentoBarbearia.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TokenResponseDto {

    @NonNull
    private String token;

    @NonNull
    private Long expirationTime;
}
