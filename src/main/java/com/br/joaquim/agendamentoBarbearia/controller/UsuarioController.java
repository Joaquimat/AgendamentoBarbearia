package com.br.joaquim.agendamentoBarbearia.controller;

import com.br.joaquim.agendamentoBarbearia.Dto.AgendamentoDto;
import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import com.br.joaquim.agendamentoBarbearia.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brb/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarUsuario(@Valid @RequestBody UsuariosDto usuariosDto)throws NotFoundException, BadRequestException {
        usuarioService.criarUsuario(usuariosDto);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable Integer id)throws NotFoundException, BadRequestException {
        usuarioService.deletarUsuario(id);
    }


}
