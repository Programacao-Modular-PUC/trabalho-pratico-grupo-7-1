package com.marau.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double valor;

    private LocalDateTime dataPagamento;

    @Column(nullable = false, length = 20)
    private String status = "PENDENTE";

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluguel_id", nullable = false)
    private Aluguel aluguel;

    public Pagamento() {}

    public Pagamento(Aluguel aluguel) {
        this.aluguel = aluguel;
        this.valor = aluguel.getValorFinal();
        this.status = "PENDENTE";
    }

    public void confirmar() {
        this.status = "CONFIRMADO";
        this.dataPagamento = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = "CANCELADO";
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Aluguel getAluguel() { return aluguel; }
    public void setAluguel(Aluguel aluguel) { this.aluguel = aluguel; }
}
