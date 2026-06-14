package com.marau.hospedagem.dto;

/**
 * DTO unificado para criação de todos os tipos de quarto.
 * Campos específicos de cada tipo só precisam ser preenchidos
 * quando o tipo correspondente for informado.
 */
public class QuartoDTO {

    private Long residenciaId;
    private String tipo; // INDIVIDUAL, DUPLO, FAMILIA
    private String identificacao;
    private Double valorBase;
    private Boolean possuiAr = false;
    private Boolean possuiHidro = false;
    private Double adicionalAr = 0.0;
    private Double adicionalHidro = 0.0;

    // QuartoIndividual
    private Integer quantidadeCamas;
    private Double adicionalPorCama;

    // QuartoDuplo
    private String tipoCama; // CASAL_COMUM, QUEEN, KING
    private Double taxaBerco;
    private Double adicionalConforto;

    // QuartoFamilia
    private Integer camasSolteiro;
    private Integer camasCasal;
    private Integer camasQueenKing;
    private Integer quantidadeAmbientes;
    private Double percentualPorHospede;

    // Getters e Setters
    public Long getResidenciaId() { return residenciaId; }
    public void setResidenciaId(Long residenciaId) { this.residenciaId = residenciaId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

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

    public Integer getQuantidadeCamas() { return quantidadeCamas; }
    public void setQuantidadeCamas(Integer quantidadeCamas) { this.quantidadeCamas = quantidadeCamas; }

    public Double getAdicionalPorCama() { return adicionalPorCama; }
    public void setAdicionalPorCama(Double adicionalPorCama) { this.adicionalPorCama = adicionalPorCama; }

    public String getTipoCama() { return tipoCama; }
    public void setTipoCama(String tipoCama) { this.tipoCama = tipoCama; }

    public Double getTaxaBerco() { return taxaBerco; }
    public void setTaxaBerco(Double taxaBerco) { this.taxaBerco = taxaBerco; }

    public Double getAdicionalConforto() { return adicionalConforto; }
    public void setAdicionalConforto(Double adicionalConforto) { this.adicionalConforto = adicionalConforto; }

    public Integer getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public Integer getCamasCasal() { return camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(Integer quantidadeAmbientes) { this.quantidadeAmbientes = quantidadeAmbientes; }

    public Double getPercentualPorHospede() { return percentualPorHospede; }
    public void setPercentualPorHospede(Double percentualPorHospede) { this.percentualPorHospede = percentualPorHospede; }
}
