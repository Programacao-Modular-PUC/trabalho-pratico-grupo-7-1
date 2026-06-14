package com.marau.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marau.hospedagem.exception.RecursoNaoPermitidoException;
import com.marau.hospedagem.model.enums.StatusQuarto;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_quarto", discriminatorType = DiscriminatorType.STRING)
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identificacao;

    @Column(nullable = false)
    private Double valorBase;

    @Column(nullable = false)
    private Boolean possuiAr = false;

    @Column(nullable = false)
    private Boolean possuiHidro = false;

    private Double adicionalAr = 0.0;

    private Double adicionalHidro = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuarto status = StatusQuarto.LIVRE;

    @JsonIgnore
    @com.fasterxml.jackson.annotation.JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residencia_id")
    private Residencia residencia;

    public Quarto() {}

    public Quarto(String identificacao, Double valorBase, Boolean possuiAr,
                  Boolean possuiHidro, Double adicionalAr, Double adicionalHidro) {
        this.identificacao = identificacao;
        this.valorBase = valorBase;
        this.possuiAr = possuiAr;
        this.possuiHidro = possuiHidro;
        this.adicionalAr = adicionalAr != null ? adicionalAr : 0.0;
        this.adicionalHidro = adicionalHidro != null ? adicionalHidro : 0.0;
    }

    /**
     * Calcula o valor da diária considerando valor base + adicionais do tipo.
     * Cada subclasse sobrescreve para aplicar suas regras específicas.
     */
    public abstract double calcularValorDiaria();

    /**
     * Retorna a capacidade máxima de hóspedes do quarto.
     */
    public abstract int getCapacidadeMaxima();

    /**
     * Retorna o tipo do quarto como String.
     */
    public abstract String getTipo();

    /**
     * Calcula valor base + adicionais comuns (ar e hidro).
     */
    protected double calcularBaseComAdicionais() {
        double valor = valorBase;
        if (Boolean.TRUE.equals(possuiAr)) {
            valor += adicionalAr;
        }
        if (Boolean.TRUE.equals(possuiHidro)) {
            valor += adicionalHidro;
        }
        return valor;
    }

    /**
     * Verifica se o quarto está disponível em um período.
     */
    public boolean isDisponivel(LocalDateTime entrada, LocalDateTime saida) {
        return status == StatusQuarto.LIVRE;
    }

    /**
     * Solicita um berço para o quarto.
     *
     * <p>Comportamento padrão: berço <b>não</b> é permitido. Os tipos de quarto
     * que suportam berço (ex.: {@link QuartoDuplo}) devem sobrescrever este método.</p>
     *
     * @throws RecursoNaoPermitidoException sempre, no comportamento padrão.
     */
    public void solicitarBerco() {
        throw new RecursoNaoPermitidoException(
                "Berço não é permitido em quarto do tipo " + getTipo() + " (" + identificacao + ").");
    }

    /**
     * Remove a solicitação de berço. Por padrão não há berço a remover.
     */
    public void removerBerco() {
        // Sem efeito para quartos que não suportam berço.
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public Boolean getPossuiAr() { return possuiAr; }
    public void setPossuiAr(Boolean possuiAr) { this.possuiAr = possuiAr; }

    public Boolean getPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(Boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Double getAdicionalAr() { return adicionalAr; }
    public void setAdicionalAr(Double adicionalAr) { this.adicionalAr = adicionalAr; }

    public Double getAdicionalHidro() { return adicionalHidro; }
    public void setAdicionalHidro(Double adicionalHidro) { this.adicionalHidro = adicionalHidro; }

    public StatusQuarto getStatus() { return status; }
    public void setStatus(StatusQuarto status) { this.status = status; }

    public Residencia getResidencia() { return residencia; }
    public void setResidencia(Residencia residencia) { this.residencia = residencia; }
}
