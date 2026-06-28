package com.marau.hospedagem.model.fidelidade;

/**
 * Benefício: direito a upgrade de quarto (quando houver disponibilidade
 * de categoria superior).
 */
public class UpgradeQuartoDecorator extends BeneficioDecorator {

    public UpgradeQuartoDecorator(CalculoBeneficios componente) {
        super(componente);
    }

    @Override
    protected void aplicarBeneficio(BeneficiosConcedidos beneficios, ContextoFidelidade contexto) {
        beneficios.concederUpgrade("Upgrade de quarto sujeito à disponibilidade");
    }
}
