package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.AgendamentoDto;
import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.ServicoEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IAgendamentoRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IServicoRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final IAgendamentoRepository agendamentoRepository;
    private final IServicoRepository servicoRepository;
    private final IUsuariosRepository usuariosRepository;

    public void criarAgendamento(AgendamentoDto agendamentoDto) throws NotFoundException {
        ServicoEntity servico = servicoRepository.findById(agendamentoDto.getServicoId())
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));

        UsuariosEntity usuario = usuariosRepository.findById(agendamentoDto.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        List<AgendamentoEntity> conflitos = agendamentoRepository.findByDataInicioLessThanAndDataFimGreaterThan(
                agendamentoDto.getDataFim(),
                agendamentoDto.getDataInicio());

        if (!conflitos.isEmpty()) {
            throw new BadRequestException("Horário indisponível para agendamento");
        } else {


            AgendamentoEntity agendamento = AgendamentoEntity.builder()
                    .usuario(usuario)
                    .servico(servico)
                    .dataInicio(agendamentoDto.getDataInicio())
                    .dataFim(agendamentoDto.getDataFim())
                    .build();

            agendamentoRepository.save(agendamento);
        }
    }
    public List<AgendamentoDto> getAgendamentos() {


        return agendamentoRepository.findAll().stream()
                .map(agendamento -> AgendamentoDto.builder()
                        .Id(agendamento.getId())
                        .dataInicio(agendamento.getDataInicio())
                        .dataFim(agendamento.getDataFim())
                        .servicoId(agendamento.getServico().getId())
                        .usuarioId(agendamento.getUsuario().getId())
                        .build())
                .toList();
    }

}


