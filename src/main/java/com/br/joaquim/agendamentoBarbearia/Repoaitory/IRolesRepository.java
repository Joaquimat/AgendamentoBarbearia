package com.br.joaquim.agendamentoBarbearia.Repoaitory;

import com.br.joaquim.agendamentoBarbearia.Entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolesRepository extends JpaRepository<RolesEntity, Integer> {

    Optional<RolesEntity> findByNome(String role);
}
