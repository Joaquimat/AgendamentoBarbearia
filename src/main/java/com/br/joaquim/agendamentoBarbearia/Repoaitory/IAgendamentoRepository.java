package com.br.joaquim.agendamentoBarbearia.Repoaitory;

import com.br.joaquim.agendamentoBarbearia.Entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IAgendamentoRepository extends JpaRepository<AgendamentoEntity, Integer> {

    List<AgendamentoEntity> findByDataInicioLessThanAndDataFimGreaterThan(
            LocalDateTime dataFim,
            LocalDateTime dataInicio
    );
}
