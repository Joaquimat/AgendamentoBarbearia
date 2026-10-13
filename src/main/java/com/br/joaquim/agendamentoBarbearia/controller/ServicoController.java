package com.br.joaquim.agendamentoBarbearia.controller;

import com.br.joaquim.agendamentoBarbearia.Dto.ServicoDto;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import com.br.joaquim.agendamentoBarbearia.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brb/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarServico(@Valid @RequestBody ServicoDto servicoDto)throws BadRequestException, NotFoundException {
        servicoService.criarServico(servicoDto);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ServicoDto> getServicos()throws NotFoundException, BadRequestException {
        return servicoService.getServicos();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarServico(@PathVariable Integer id)throws NotFoundException, BadRequestException {
        servicoService.deletarServico(id);
    }

}
