package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.AgendamentoDto;
import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IAgendamentoRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuariosRepository usuariosRepository;
    private final IAgendamentoRepository agendamentoRepository;

    public void criarUsuario(UsuariosDto usuariosDto) throws BadRequestException {
        UsuariosEntity usuarios = usuariosRepository.findByEmail(usuariosDto.getEmail())
                .orElse(null);
        if (usuarios != null) {
            throw new BadRequestException("Usuário já existe");
        }
        usuariosRepository.save(UsuariosEntity.builder()
                .nome(usuariosDto.getNome())
                .email(usuariosDto.getEmail())
                .senha(usuariosDto.getSenha())
                .build());
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDto> getUsuarioAgendamentos(Integer id) throws NotFoundException {
        UsuariosEntity usuarios = usuariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        return usuarios.getAgendamentos().stream()
                .map(agendamento -> AgendamentoDto.builder()
                        .Id(agendamento.getId())
                        .usuarioId(agendamento.getUsuario().getId())
                        .servicoId(agendamento.getServico().getId())
                        .dataInicio(agendamento.getDataInicio())
                        .dataFim(agendamento.getDataFim())
                        .build())
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletarUsuario(Integer id) throws NotFoundException {
        UsuariosEntity usuarios = usuariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        usuariosRepository.deleteById(id);

    }


}
