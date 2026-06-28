package com.marau.hospedagem.dto;

import java.time.LocalDate;

/**
 * Requisição para cadastrar uma nova promoção temporária (regra de tarifa)
 * em tempo de execução.
 */
public class PromocaoDTO {

    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
    private double percentualDesconto;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }

    public double getPercentualDesconto() { return percentualDesconto; }
    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }
}
