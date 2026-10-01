package com.br.joaquim.agendamentoBarbearia.controller;

import com.br.joaquim.agendamentoBarbearia.Dto.LoginRequestDto;
import com.br.joaquim.agendamentoBarbearia.Dto.RegisterRequestDto;
import com.br.joaquim.agendamentoBarbearia.Dto.TokenResponseDto;
import com.br.joaquim.agendamentoBarbearia.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brb/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterRequestDto registerRequestDto)throws Exception {
        authenticationService.register(registerRequestDto);

    }
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody @Valid LoginRequestDto LoginRequestDto)throws Exception {
        return authenticationService.login(LoginRequestDto);

    }
}
