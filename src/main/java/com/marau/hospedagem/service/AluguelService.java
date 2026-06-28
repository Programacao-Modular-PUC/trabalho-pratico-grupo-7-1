package com.marau.hospedagem.service;

import com.marau.hospedagem.dto.AluguelDTO;
import com.marau.hospedagem.exception.EntidadeNaoEncontradaException;
import com.marau.hospedagem.exception.QuartoIndisponivelException;
import com.marau.hospedagem.model.*;
import com.marau.hospedagem.model.enums.StatusAluguel;
import com.marau.hospedagem.model.enums.StatusQuarto;
import com.marau.hospedagem.model.tarifa.ContextoTarifa;
import com.marau.hospedagem.repository.AluguelRepository;
import com.marau.hospedagem.repository.ClienteRepository;
import com.marau.hospedagem.repository.QuartoRepository;
import com.marau.hospedagem.repository.ResidenciaRepository;
import com.marau.hospedagem.service.tarifa.GerenciadorTarifas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ResidenciaRepository residenciaRepository;
    private final QuartoRepository quartoRepository;
    private final ClienteRepository clienteRepository;

    public AluguelService(AluguelRepository aluguelRepository,
                          ResidenciaRepository residenciaRepository,
                          QuartoRepository quartoRepository,
                          ClienteRepository clienteRepository) {
        this.aluguelRepository = aluguelRepository;
        this.residenciaRepository = residenciaRepository;
        this.quartoRepository = quartoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Aluguel> listarTodos() {
        return aluguelRepository.findAll();
    }

    public List<Aluguel> listarPorResidencia(Long residenciaId) {
        return aluguelRepository.findByResidenciaId(residenciaId);
    }

    public List<Aluguel> listarPorCliente(Long clienteId) {
        return aluguelRepository.findByClienteId(clienteId);
    }

    /**
     * Histórico de aluguéis de um cliente (NOVO REQUISITO - Sprint 3).
     * Valida a existência do cliente antes de retornar o histórico.
     *
     * @throws EntidadeNaoEncontradaException se o cliente não existir.
     */
    public List<Aluguel> historicoPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", clienteId));
        return aluguelRepository.findByClienteId(clienteId);
    }

    public Aluguel buscarPorId(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluguel", id));
    }

    /**
     * Cria um novo aluguel com todas as validações:
     * - Quarto deve estar disponível
     * - Se QuartoDuplo e solicitou berço, aplica berço
     * - Calcula diárias e valor final
     * - Gera pagamento e recibo
     * - Marca quarto como RESERVADO
     */
    @Transactional
    public Aluguel criar(AluguelDTO dto) {
        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Residência", dto.getResidenciaId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Quarto", dto.getQuartoId()));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", dto.getClienteId()));

        // Validar disponibilidade do quarto
        if (!quarto.isDisponivel(dto.getDataEntrada(), dto.getDataSaida())) {
            throw QuartoIndisponivelException.paraQuarto(quarto.getIdentificacao());
        }

        // Berço: chamada polimórfica. Lança RecursoNaoPermitidoException se o
        // tipo de quarto não suportar berço (ex.: individual ou família).
        if (Boolean.TRUE.equals(dto.getSolicitarBerco())) {
            quarto.solicitarBerco();
        }

        // Criar aluguel
        Aluguel aluguel = new Aluguel(residencia, quarto, cliente,
                dto.getDataEntrada(), dto.getDataSaida(), dto.getNumHospedes());

        // Tarifação flexível (Strategy + Singleton): ajusta a diária conforme as
        // regras vigentes (temporada, feriado, promoção, cliente frequente).
        aplicarTarifacao(aluguel);

        // Gerar pagamento (já com o valor tarifado)
        Pagamento pagamento = new Pagamento(aluguel);
        aluguel.setPagamento(pagamento);

        // Gerar recibo
        aluguel.gerarRecibo();

        // Marcar quarto como reservado
        quarto.setStatus(StatusQuarto.RESERVADO);
        quartoRepository.save(quarto);

        return aluguelRepository.save(aluguel);
    }

    /**
     * Aplica as regras de tarifação flexível sobre o aluguel recém-criado.
     *
     * <p>Monta o {@link ContextoTarifa} (data de entrada, diária base do quarto,
     * número de diárias e histórico do cliente) e delega o cálculo ao
     * {@link GerenciadorTarifas} (Singleton). O resultado redefine o valor da
     * diária e o valor final, que serão usados por pagamento e recibo.</p>
     */
    private void aplicarTarifacao(Aluguel aluguel) {
        int totalHospedagensCliente = 0;
        if (aluguel.getCliente() != null && aluguel.getCliente().getId() != null) {
            totalHospedagensCliente = (int) aluguelRepository
                    .findByClienteId(aluguel.getCliente().getId()).stream()
                    .filter(a -> a.getStatus() != StatusAluguel.CANCELADO)
                    .count();
        }

        ContextoTarifa contexto = ContextoTarifa.builder()
                .dataReferencia(aluguel.getDataEntrada().toLocalDate())
                .valorDiariaBase(aluguel.getValorDiaria())
                .quantidadeDiarias(aluguel.getQuantidadeDiarias())
                .totalHospedagensCliente(totalHospedagensCliente)
                .tipoQuarto(aluguel.getQuarto().getTipo())
                .build();

        double diariaTarifada = GerenciadorTarifas.getInstance().calcularValorDiaria(contexto);
        aluguel.setValorDiaria(diariaTarifada);
        aluguel.setValorFinal(diariaTarifada * aluguel.getQuantidadeDiarias());
    }

    /**
     * Confirma um aluguel (RESERVADO -> ATIVO).
     * Marca o quarto como OCUPADO.
     */
    @Transactional
    public Aluguel confirmar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.confirmar();
        aluguel.getQuarto().setStatus(StatusQuarto.OCUPADO);
        quartoRepository.save(aluguel.getQuarto());
        return aluguelRepository.save(aluguel);
    }

    /**
     * Encerra um aluguel (ATIVO -> ENCERRADO).
     * Libera o quarto.
     */
    @Transactional
    public Aluguel encerrar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.encerrar();
        aluguel.getQuarto().setStatus(StatusQuarto.LIVRE);

        // Se QuartoDuplo, remove berço após encerramento
        if (aluguel.getQuarto() instanceof QuartoDuplo quartoDuplo) {
            quartoDuplo.removerBerco();
        }

        quartoRepository.save(aluguel.getQuarto());
        return aluguelRepository.save(aluguel);
    }

    /**
     * Cancela um aluguel e libera o quarto.
     */
    @Transactional
    public Aluguel cancelar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguel.cancelar();
        aluguel.getQuarto().setStatus(StatusQuarto.LIVRE);

        if (aluguel.getQuarto() instanceof QuartoDuplo quartoDuplo) {
            quartoDuplo.removerBerco();
        }

        quartoRepository.save(aluguel.getQuarto());
        if (aluguel.getPagamento() != null) {
            aluguel.getPagamento().cancelar();
        }
        return aluguelRepository.save(aluguel);
    }

    /**
     * Retorna o recibo formatado de um aluguel.
     */
    public String getRecibo(Long id) {
        Aluguel aluguel = buscarPorId(id);
        if (aluguel.getRecibo() == null) {
            aluguel.gerarRecibo();
            aluguelRepository.save(aluguel);
        }
        return aluguel.getRecibo().imprimir();
    }
}
