package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.LoginRequestDto;
import com.br.joaquim.agendamentoBarbearia.Dto.RegisterRequestDto;
import com.br.joaquim.agendamentoBarbearia.Dto.TokenResponseDto;
import com.br.joaquim.agendamentoBarbearia.Dto.UsuariosDto;
import com.br.joaquim.agendamentoBarbearia.Entity.RolesEntity;
import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IRolesRepository;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IUsuariosRepository;
import com.br.joaquim.agendamentoBarbearia.config.TokenProvider;
import com.br.joaquim.agendamentoBarbearia.enums.RoleTypeEnum;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IUsuariosRepository usuariosRepository;
    private final IRolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Value("${JWT_EXPIRATION:900000}")
    private Long expirationTime;

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

    public TokenResponseDto login(LoginRequestDto loginRequestDto) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (loginRequestDto.getEmail(), loginRequestDto.getSenha()));
            String token = tokenProvider.gerarToken(authentication);

            return new TokenResponseDto(token, expirationTime);

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Usuário ou senha inválidos");
        } catch (Exception e) {
            throw new Exception("Erro ao autenticar usuário");
        }
    }
}
