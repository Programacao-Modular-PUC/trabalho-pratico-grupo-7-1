package com.marau.hospedagem.model.tarifa;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado detalhado de uma tarifação, usado para demonstrar (e auditar) quais
 * regras incidiram sobre a diária e o impacto total no aluguel.
 */
public class ResultadoTarifa {

    private final double valorDiariaBase;
    private double valorDiariaFinal;
    private final int quantidadeDiarias;
    private final List<String> regrasAplicadas = new ArrayList<>();

    public ResultadoTarifa(double valorDiariaBase, int quantidadeDiarias) {
        this.valorDiariaBase = valorDiariaBase;
        this.valorDiariaFinal = valorDiariaBase;
        this.quantidadeDiarias = quantidadeDiarias;
    }

    public void registrarRegra(String nome, double valorDiariaFinal) {
        this.regrasAplicadas.add(nome);
        this.valorDiariaFinal = valorDiariaFinal;
    }

    public double getValorDiariaBase() { return valorDiariaBase; }
    public double getValorDiariaFinal() { return valorDiariaFinal; }
    public int getQuantidadeDiarias() { return quantidadeDiarias; }
    public List<String> getRegrasAplicadas() { return regrasAplicadas; }

    public double getValorTotal() {
        return valorDiariaFinal * quantidadeDiarias;
    }

    public double getValorTotalBase() {
        return valorDiariaBase * quantidadeDiarias;
    }
}
