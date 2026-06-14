package com.marau.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formato do recibo exigido pelo enunciado:
 * - Data e horário de entrada
 * - Data e horário de saída
 * - Número de diárias
 * - Total a pagar
 */
@Entity
@Table(name = "recibos")
public class Recibo {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH'h'mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataEmissao;

    private Double valorTotal;
    private Integer quantidadeDiarias;
    private String nomeCliente;
    private String descricaoQuarto;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluguel_id", nullable = false)
    private Aluguel aluguel;

    public Recibo() {}

    public Recibo(Aluguel aluguel) {
        this.aluguel = aluguel;
        this.dataEmissao = LocalDateTime.now();
        this.valorTotal = aluguel.getValorFinal();
        this.quantidadeDiarias = aluguel.getQuantidadeDiarias();
        this.nomeCliente = aluguel.getCliente().getNome();
        this.descricaoQuarto = aluguel.getQuarto().getIdentificacao() + " - " + aluguel.getQuarto().getTipo();
    }

    /**
     * Imprime o recibo no formato exigido pelo enunciado.
     */
    public String imprimir() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         RECIBO DE HOSPEDAGEM           \n");
        sb.append("========================================\n");
        sb.append("Cliente: ").append(nomeCliente).append("\n");
        sb.append("Quarto: ").append(descricaoQuarto).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Data e horário de entrada: ").append(formatarDataEntrada()).append("\n");
        sb.append("Data e horário de saída:   ").append(formatarDataSaida()).append("\n");
        sb.append("Número de diárias:         ").append(quantidadeDiarias).append("\n");
        sb.append("Total à pagar:             R$ ").append(String.format("%.2f", valorTotal)).append("\n");
        sb.append("========================================\n");
        sb.append("Emitido em: ").append(dataEmissao.format(FMT)).append("\n");
        return sb.toString();
    }

    public String formatarDataEntrada() {
        return aluguel.getDataEntrada().format(FMT);
    }

    public String formatarDataSaida() {
        return aluguel.getDataSaida().format(FMT);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Integer getQuantidadeDiarias() { return quantidadeDiarias; }
    public void setQuantidadeDiarias(Integer quantidadeDiarias) { this.quantidadeDiarias = quantidadeDiarias; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getDescricaoQuarto() { return descricaoQuarto; }
    public void setDescricaoQuarto(String descricaoQuarto) { this.descricaoQuarto = descricaoQuarto; }

    public Aluguel getAluguel() { return aluguel; }
    public void setAluguel(Aluguel aluguel) { this.aluguel = aluguel; }
}
