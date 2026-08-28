package com.br.joaquim.agendamentoBarbearia.Repoaitory;

import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAgendamentoRepository extends JpaRepository<AgendamentoEntity, Integer> {
}
