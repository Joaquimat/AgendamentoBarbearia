package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Repoaitory.IAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final IAgendamentoRepository agendamentoRepository;


}
