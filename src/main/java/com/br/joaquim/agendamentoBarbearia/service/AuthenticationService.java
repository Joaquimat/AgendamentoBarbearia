package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.RegisterRequestDto;
import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.Entity.RolesEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IRolesRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.enums.RoleTypeEnum;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUsuariosRepository usuariosRepository;
    private final IRolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDto dto) throws BadRequestException {
        UsuariosEntity usuarios = usuariosRepository.findByEmail(dto.getEmail())
                .orElse(null);
        if (usuarios != null) {
            throw new BadRequestException("Usuário já existe");
        }
        RolesEntity rolesEntity = rolesRepository.findByNome(RoleTypeEnum.ROLE_USER.name())
                        .orElseGet(() -> rolesRepository.save(RolesEntity.builder()
                                .nome(RoleTypeEnum.ROLE_USER.name())
                                .build()));

        usuariosRepository.save(UsuariosEntity.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .roles(Set.of(rolesEntity))
                .senha(passwordEncoder.encode(dto.getSenha()))
                .build());
    }
}
