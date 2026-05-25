package com.marau.hospedagem.service;

import com.marau.hospedagem.dto.QuartoDTO;
import com.marau.hospedagem.model.*;
import com.marau.hospedagem.model.enums.StatusQuarto;
import com.marau.hospedagem.model.enums.TipoCamaCasal;
import com.marau.hospedagem.repository.QuartoRepository;
import com.marau.hospedagem.repository.ResidenciaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository quartoRepository, ResidenciaRepository residenciaRepository) {
        this.quartoRepository = quartoRepository;
        this.residenciaRepository = residenciaRepository;
    }

    public List<Quarto> listarPorResidencia(Long residenciaId) {
        return quartoRepository.findByResidenciaId(residenciaId);
    }

    public List<Quarto> listarDisponiveis(Long residenciaId) {
        return quartoRepository.findByResidenciaIdAndStatus(residenciaId, StatusQuarto.LIVRE);
    }

    public Quarto buscarPorId(Long id) {
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quarto não encontrado: " + id));
    }

    /**
     * Cria um quarto com base no tipo informado no DTO.
     */
    public Quarto criar(QuartoDTO dto) {
        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
                .orElseThrow(() -> new RuntimeException("Residência não encontrada: " + dto.getResidenciaId()));

        Quarto quarto = criarPorTipo(dto);
        residencia.adicionarQuarto(quarto);
        return quartoRepository.save(quarto);
    }

    /**
     * Factory method para criar o tipo correto de quarto.
     */
    private Quarto criarPorTipo(QuartoDTO dto) {
        return switch (dto.getTipo().toUpperCase()) {
            case "INDIVIDUAL" -> new QuartoIndividual(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    dto.getQuantidadeCamas(), dto.getAdicionalPorCama()
            );
            case "DUPLO" -> new QuartoDuplo(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    dto.getTipoCama() != null ? TipoCamaCasal.valueOf(dto.getTipoCama()) : TipoCamaCasal.CASAL_COMUM,
                    dto.getTaxaBerco(), dto.getAdicionalConforto()
            );
            case "FAMILIA" -> new QuartoFamilia(
                    dto.getIdentificacao(), dto.getValorBase(),
                    dto.getPossuiAr(), dto.getPossuiHidro(),
                    dto.getAdicionalAr(), dto.getAdicionalHidro(),
                    dto.getCamasSolteiro(), dto.getCamasCasal(),
                    dto.getCamasQueenKing(), dto.getQuantidadeAmbientes(),
                    dto.getPercentualPorHospede()
            );
            default -> throw new IllegalArgumentException("Tipo de quarto inválido: " + dto.getTipo());
        };
    }

    public void deletar(Long id) {
        buscarPorId(id);
        quartoRepository.deleteById(id);
    }
}
