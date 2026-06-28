package com.marau.hospedagem.model.fidelidade;

/**
 * Benefício: check-out estendido (atraso autorizado na saída sem cobrança de
 * diária adicional).
 */
public class CheckoutEstendidoDecorator extends BeneficioDecorator {

    private final int horas;

    public CheckoutEstendidoDecorator(CalculoBeneficios componente, int horas) {
        super(componente);
        this.horas = horas;
    }

    @Override
    protected void aplicarBeneficio(BeneficiosConcedidos beneficios, ContextoFidelidade contexto) {
        beneficios.adicionarHorasCheckout(horas,
                String.format("Check-out estendido em %d hora(s)", horas));
    }
}
