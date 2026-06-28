package com.marau.hospedagem.model.fidelidade;

import java.util.ArrayList;
import java.util.List;

/**
 * Acumulador dos benefícios concedidos a um cliente. É o objeto que trafega
 * pela cadeia de decoradores: cada decorador adiciona o seu benefício a esta
 * estrutura.
 */
public class BeneficiosConcedidos {

    private double percentualDesconto = 0.0;
    private boolean upgradeQuarto = false;
    private int horasCheckoutEstendido = 0;
    private int diariasGratuitas = 0;
    private final List<String> descricoes = new ArrayList<>();

    public void adicionarDesconto(double percentual, String descricao) {
        // Descontos acumulam de forma composta (sobre o que sobra).
        this.percentualDesconto = 1 - (1 - this.percentualDesconto) * (1 - percentual);
        this.descricoes.add(descricao);
    }

    public void concederUpgrade(String descricao) {
        this.upgradeQuarto = true;
        this.descricoes.add(descricao);
    }

    public void adicionarHorasCheckout(int horas, String descricao) {
        this.horasCheckoutEstendido += horas;
        this.descricoes.add(descricao);
    }

    public void adicionarDiariasGratuitas(int diarias, String descricao) {
        this.diariasGratuitas += diarias;
        this.descricoes.add(descricao);
    }

    public double getPercentualDesconto() { return percentualDesconto; }
    public boolean isUpgradeQuarto() { return upgradeQuarto; }
    public int getHorasCheckoutEstendido() { return horasCheckoutEstendido; }
    public int getDiariasGratuitas() { return diariasGratuitas; }
    public List<String> getDescricoes() { return descricoes; }
}
