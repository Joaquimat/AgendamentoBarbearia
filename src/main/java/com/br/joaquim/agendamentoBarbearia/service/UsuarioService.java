package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IAgendamentoRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public AgendamentoEntity getUsuarioAgendamento(Integer id) throws NotFoundException {
        UsuariosEntity usuarios = usuariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        AgendamentoEntity agendamento = usuarios.getAgendamento();
        if (agendamento == null) {
            throw new BadRequestException("Agendamento não encontrado para o usuário");
        }
        return agendamento;
    }


}
