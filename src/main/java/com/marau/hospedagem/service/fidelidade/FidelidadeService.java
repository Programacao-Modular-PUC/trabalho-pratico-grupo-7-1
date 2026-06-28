package com.marau.hospedagem.service.fidelidade;

import com.marau.hospedagem.dto.ResultadoFidelidadeDTO;
import com.marau.hospedagem.exception.EntidadeNaoEncontradaException;
import com.marau.hospedagem.model.Cliente;
import com.marau.hospedagem.model.enums.StatusAluguel;
import com.marau.hospedagem.model.fidelidade.BeneficiosConcedidos;
import com.marau.hospedagem.model.fidelidade.CalculoBeneficios;
import com.marau.hospedagem.model.fidelidade.CategoriaFidelidade;
import com.marau.hospedagem.model.fidelidade.ContextoFidelidade;
import com.marau.hospedagem.repository.AluguelRepository;
import com.marau.hospedagem.repository.ClienteRepository;
import org.springframework.stereotype.Service;

/**
 * Serviço do programa de fidelidade. Calcula a categoria do cliente a partir do
 * seu histórico de hospedagens e monta os benefícios correspondentes usando a
 * {@link FidelidadeFactory} (Factory Method + Decorator).
 */
@Service
public class FidelidadeService {

    private final ClienteRepository clienteRepository;
    private final AluguelRepository aluguelRepository;

    public FidelidadeService(ClienteRepository clienteRepository, AluguelRepository aluguelRepository) {
        this.clienteRepository = clienteRepository;
        this.aluguelRepository = aluguelRepository;
    }

    /**
     * Conta as hospedagens válidas (não canceladas) de um cliente.
     */
    public int contarHospedagens(Long clienteId) {
        return (int) aluguelRepository.findByClienteId(clienteId).stream()
                .filter(a -> a.getStatus() != StatusAluguel.CANCELADO)
                .count();
    }

    /**
     * Consulta a categoria e os benefícios de fidelidade de um cliente.
     */
    public ResultadoFidelidadeDTO consultar(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", clienteId));

        int totalHospedagens = contarHospedagens(clienteId);
        CategoriaFidelidade categoria = CategoriaFidelidade.porHospedagens(totalHospedagens);

        // Contexto nominal (1 diária) para evidenciar o direito à diária gratuita.
        ContextoFidelidade contexto = new ContextoFidelidade(totalHospedagens, 0.0, 1);
        BeneficiosConcedidos beneficios = FidelidadeFactory.criarBeneficios(categoria).calcular(contexto);

        return ResultadoFidelidadeDTO.de(clienteId, cliente.getNome(), totalHospedagens, categoria, beneficios);
    }

    /**
     * Simula a aplicação dos benefícios sobre uma hospedagem hipotética,
     * calculando o valor final com o desconto de fidelidade.
     */
    public ResultadoFidelidadeDTO simular(Long clienteId, double valor, int diarias) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", clienteId));

        int totalHospedagens = contarHospedagens(clienteId);
        CategoriaFidelidade categoria = CategoriaFidelidade.porHospedagens(totalHospedagens);

        ContextoFidelidade contexto = new ContextoFidelidade(totalHospedagens, valor, diarias);
        CalculoBeneficios calculo = FidelidadeFactory.criarBeneficios(categoria);
        BeneficiosConcedidos beneficios = calculo.calcular(contexto);

        double valorComDesconto = aplicarBeneficiosFinanceiros(valor, diarias, beneficios);

        ResultadoFidelidadeDTO dto =
                ResultadoFidelidadeDTO.de(clienteId, cliente.getNome(), totalHospedagens, categoria, beneficios);
        dto.aplicarValores(valor, valorComDesconto);
        return dto;
    }

    /**
     * Aplica desconto percentual e diárias gratuitas sobre o valor da estadia.
     */
    private double aplicarBeneficiosFinanceiros(double valor, int diarias, BeneficiosConcedidos beneficios) {
        double valorDiaria = diarias > 0 ? valor / diarias : valor;
        double valorAposDiariasGratis = valor - (valorDiaria * beneficios.getDiariasGratuitas());
        if (valorAposDiariasGratis < 0) {
            valorAposDiariasGratis = 0;
        }
        return valorAposDiariasGratis * (1 - beneficios.getPercentualDesconto());
    }
}
