package com.marau.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.marau.hospedagem.model.enums.StatusAluguel;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Regras de negócio:
 * - Diárias sempre iniciam às 12h
 * - Entrada após 12h → conta como diária completa
 * - Saída após 12h → adiciona nova diária
 * - Valor final = valorDiaria * quantidadeDiarias
 */
@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"quartos", "historicoAlugueis", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id", nullable = false)
    private Residencia residencia;

    @JsonIgnoreProperties({"residencia", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @JsonIgnoreProperties({"historicoAlugueis", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime dataEntrada;

    @Column(nullable = false)
    private LocalDateTime dataSaida;

    private Integer quantidadeDiarias;

    private Double valorDiaria;

    private Double valorFinal;

    /**
     * Número de hóspedes (relevante para QuartoFamilia).
     */
    private Integer numHospedes = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluguel status = StatusAluguel.RESERVADO;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @OneToOne(mappedBy = "aluguel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    @OneToOne(mappedBy = "aluguel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Recibo recibo;

    public Aluguel() {}

    public Aluguel(Residencia residencia, Quarto quarto, Cliente cliente,
                   LocalDateTime dataEntrada, LocalDateTime dataSaida, Integer numHospedes) {
        this.residencia = residencia;
        this.quarto = quarto;
        this.cliente = cliente;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.numHospedes = numHospedes != null ? numHospedes : 1;
        this.quantidadeDiarias = calcularDiarias();
        this.valorDiaria = calcularValorDiariaQuarto();
        this.valorFinal = calcularValorFinal();
    }

    /**
     * Calcula o número de diárias com base na regra das 12h.
     * - Conta os dias entre entrada e saída
     * - Entrada após 12h → conta como diária completa (já incluso)
     * - Saída após 12h → adiciona 1 diária extra
     */
    public int calcularDiarias() {
        if (dataEntrada == null || dataSaida == null) return 0;

        long dias = ChronoUnit.DAYS.between(dataEntrada.toLocalDate(), dataSaida.toLocalDate());
        if (dias < 1) dias = 1;

        // Saída após 12h adiciona nova diária
        if (dataSaida.getHour() > 12 ||
            (dataSaida.getHour() == 12 && dataSaida.getMinute() > 0)) {
            dias++;
        }

        return (int) dias;
    }

    /**
     * Obtém o valor da diária do quarto, considerando tipo.
     * Para QuartoFamilia, usa cálculo por número de hóspedes.
     */
    private double calcularValorDiariaQuarto() {
        if (quarto instanceof QuartoFamilia quartoFamilia) {
            return quartoFamilia.calcularComHospedes(numHospedes);
        }
        return quarto.calcularValorDiaria();
    }

    /**
     * Calcula o valor final do aluguel.
     */
    public double calcularValorFinal() {
        this.quantidadeDiarias = calcularDiarias();
        this.valorDiaria = calcularValorDiariaQuarto();
        this.valorFinal = valorDiaria * quantidadeDiarias;
        return this.valorFinal;
    }

    /**
     * Gera o recibo associado ao aluguel.
     */
    public Recibo gerarRecibo() {
        Recibo novoRecibo = new Recibo(this);
        this.recibo = novoRecibo;
        return novoRecibo;
    }

    /**
     * Confirma o aluguel (de RESERVADO para ATIVO).
     */
    public void confirmar() {
        if (this.status != StatusAluguel.RESERVADO) {
            throw new IllegalStateException("Apenas aluguéis reservados podem ser confirmados.");
        }
        this.status = StatusAluguel.ATIVO;
    }

    /**
     * Encerra o aluguel.
     */
    public void encerrar() {
        if (this.status != StatusAluguel.ATIVO) {
            throw new IllegalStateException("Apenas aluguéis ativos podem ser encerrados.");
        }
        this.status = StatusAluguel.ENCERRADO;
    }

    /**
     * Cancela o aluguel.
     */
    public void cancelar() {
        if (this.status == StatusAluguel.ENCERRADO) {
            throw new IllegalStateException("Aluguéis encerrados não podem ser cancelados.");
        }
        this.status = StatusAluguel.CANCELADO;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Residencia getResidencia() { return residencia; }
    public void setResidencia(Residencia residencia) { this.residencia = residencia; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public Integer getQuantidadeDiarias() { return quantidadeDiarias; }
    public void setQuantidadeDiarias(Integer quantidadeDiarias) { this.quantidadeDiarias = quantidadeDiarias; }

    public Double getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(Double valorDiaria) { this.valorDiaria = valorDiaria; }

    public Double getValorFinal() { return valorFinal; }
    public void setValorFinal(Double valorFinal) { this.valorFinal = valorFinal; }

    public Integer getNumHospedes() { return numHospedes; }
    public void setNumHospedes(Integer numHospedes) { this.numHospedes = numHospedes; }

    public StatusAluguel getStatus() { return status; }
    public void setStatus(StatusAluguel status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }

    public Recibo getRecibo() { return recibo; }
    public void setRecibo(Recibo recibo) { this.recibo = recibo; }
}
