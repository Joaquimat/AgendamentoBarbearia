package com.br.joaquim.agendamentoBarbearia.Repoaitory;

import com.br.joaquim.agendamentoBarbearia.Entity.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuariosRepository extends JpaRepository<UsuariosEntity, Integer> {

    Optional<UsuariosEntity> findByEmail(String email);
}
