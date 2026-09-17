package com.br.joaquim.agendamentoBarbearia.controller;

import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import com.br.joaquim.agendamentoBarbearia.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brb/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarUsuario(@Valid @RequestBody UsuariosDto usuariosDto)throws NotFoundException, BadRequestException {
        usuarioService.criarUsuario(usuariosDto);
    }

    @GetMapping("/{id}/agendamentos")
    @ResponseStatus(HttpStatus.OK)
    public void getUsuarioAgendamentos(@PathVariable Integer id)throws NotFoundException, BadRequestException {
        usuarioService.getUsuarioAgendamentos(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable Integer id)throws NotFoundException, BadRequestException {
        usuarioService.deletarUsuario(id);
    }


}
