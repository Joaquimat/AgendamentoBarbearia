package com.br.joaquim.agendamentoBarbearia.controller;

import com.br.joaquim.agendamentoBarbearia.Dto.AgendamentoDto;
import com.br.joaquim.agendamentoBarbearia.Dto.ServicoDto;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import com.br.joaquim.agendamentoBarbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brb/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {


    private final AgendamentoService agendamentoService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarAgendamento(@Valid @RequestBody AgendamentoDto agendamentoDto)throws BadRequestException, NotFoundException {
        agendamentoService.criarAgendamento(agendamentoDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AgendamentoDto> getAgendamentos()throws NotFoundException, BadRequestException {
        return agendamentoService.getAgendamentos();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarAgendamento(@PathVariable Integer id)throws NotFoundException, BadRequestException {
       agendamentoService.deletarAgendamento(id);
    }
}
