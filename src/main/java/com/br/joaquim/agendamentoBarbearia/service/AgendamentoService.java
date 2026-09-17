package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.AgendamentoDto;
import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IAgendamentoRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final IAgendamentoRepository agendamentoRepository;




    }
