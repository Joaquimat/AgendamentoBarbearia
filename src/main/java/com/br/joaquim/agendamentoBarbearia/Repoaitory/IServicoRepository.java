package com.br.joaquim.agendamentoBarbearia.Repoaitory;

import com.br.joaquim.agendamentoBarbearia.Entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IServicoRepository extends JpaRepository<ServicoEntity, Integer> {
}
