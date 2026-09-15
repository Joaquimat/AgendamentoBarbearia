package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuariosRepository usuariosRepository;

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






}
