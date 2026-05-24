package com.marau.hospedagem.model;

import com.marau.hospedagem.model.enums.TipoCamaCasal;
import jakarta.persistence.*;

/**
 * Quarto Duplo (Casal):
 * - Possui cama casal comum, Queen ou King
 * - Pode ter berço (opcional, solicitado pelo cliente)
 * - Valor da diária = valorBase + adicionalConforto (por tipo de cama) + taxaBerco (se solicitado) + ar + hidro
 * - Capacidade: 2 adultos + 1 criança (se berço)
 */
@Entity
@DiscriminatorValue("DUPLO")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal")
    private TipoCamaCasal tipoCama = TipoCamaCasal.CASAL_COMUM;

    @Column(name = "possui_berco")
    private Boolean possuiBerco = false;

    @Column(name = "taxa_berco")
    private Double taxaBerco = 0.0;

    @Column(name = "adicional_conforto")
    private Double adicionalConforto = 0.0;

    public QuartoDuplo() {}

    public QuartoDuplo(String identificacao, Double valorBase, Boolean possuiAr,
                       Boolean possuiHidro, Double adicionalAr, Double adicionalHidro,
                       TipoCamaCasal tipoCama, Double taxaBerco, Double adicionalConforto) {
        super(identificacao, valorBase, possuiAr, possuiHidro, adicionalAr, adicionalHidro);
        this.tipoCama = tipoCama != null ? tipoCama : TipoCamaCasal.CASAL_COMUM;
        this.taxaBerco = taxaBerco != null ? taxaBerco : 0.0;
        this.adicionalConforto = adicionalConforto != null ? adicionalConforto : 0.0;
    }

    @Override
    public double calcularValorDiaria() {
        double valor = calcularBaseComAdicionais();
        // Adicional por conforto (tipo da cama)
        valor += adicionalConforto;
        // Taxa de berço se solicitado
        if (Boolean.TRUE.equals(possuiBerco)) {
            valor += taxaBerco;
        }
        return valor;
    }

    @Override
    public int getCapacidadeMaxima() {
        return Boolean.TRUE.equals(possuiBerco) ? 3 : 2;
    }

    @Override
    public String getTipo() {
        return "DUPLO";
    }

    /**
     * Cliente solicita berço para o quarto.
     */
    public void solicitarBerco() {
        this.possuiBerco = true;
    }

    /**
     * Remove solicitação de berço.
     */
    public void removerBerco() {
        this.possuiBerco = false;
    }

    // Getters e Setters
    public TipoCamaCasal getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCamaCasal tipoCama) { this.tipoCama = tipoCama; }

    public Boolean getPossuiBerco() { return possuiBerco; }
    public void setPossuiBerco(Boolean possuiBerco) { this.possuiBerco = possuiBerco; }

    public Double getTaxaBerco() { return taxaBerco; }
    public void setTaxaBerco(Double taxaBerco) { this.taxaBerco = taxaBerco; }

    public Double getAdicionalConforto() { return adicionalConforto; }
    public void setAdicionalConforto(Double adicionalConforto) { this.adicionalConforto = adicionalConforto; }
}
