package com.br.joaquim.agendamentoBarbearia.service;

import com.br.joaquim.agendamentoBarbearia.Dto.ServicoDto;
import com.br.joaquim.agendamentoBarbearia.Entity.ServicoEntity;
import com.br.joaquim.agendamentoBarbearia.Repoaitory.IServicoRepository;
import com.br.joaquim.agendamentoBarbearia.exception.BadRequestException;
import com.br.joaquim.agendamentoBarbearia.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ServicoService {

    private final IServicoRepository servicoRepository;

    public void criarServico(ServicoDto servicoDto) throws BadRequestException {
        ServicoEntity servico = servicoRepository.findByNome(servicoDto.getNome())
                .orElse(null);
        if (servico != null) {
            throw new BadRequestException("Serviço já existe");
        }
        servicoRepository.save(ServicoEntity.builder()
                .nome(servicoDto.getNome())
                .duracao(servicoDto.getDuracao())
                .valor(servicoDto.getValor())
                .build());
    }

    public List<ServicoDto> getServicos() {

        return servicoRepository.findAll().stream()
                .map(servico -> ServicoDto.builder()
                        .nome(servico.getNome())
                        .duracao(servico.getDuracao())
                        .valor(servico.getValor())
                        .build())
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletarServico(Integer id) throws NotFoundException {
        ServicoEntity agendamento = servicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Serviço não encontrado"));

        servicoRepository.deleteById(id);
    }
}
