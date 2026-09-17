package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.ServicoDto;
import com.br.joaquim.agendamentoBarbearia.Entity.ServicoEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final IServicoRepository servicoRepository;

    public void criarServico(ServicoDto servicoDto) {
        ServicoEntity servico = servicoRepository.findByNome(servicoDto.getNome())
                .orElse(null);
        if (servico != null) {
            throw new RuntimeException("Serviço já existe");
        }
        servicoRepository.save(ServicoEntity.builder()
                        .nome(servicoDto.getNome())
                        .duracao(servicoDto.getDuracao())
                        .valor(servicoDto.getValor())
                .build());
    }
}
