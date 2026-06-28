package com.marau.hospedagem.model.fidelidade;

import com.marau.hospedagem.service.fidelidade.FidelidadeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes do programa de fidelidade: resolução de categoria (enum) e montagem
 * dos benefícios via Factory Method + Decorator.
 */
@DisplayName("Programa de Fidelidade - categorias e benefícios (Decorator + Factory)")
class FidelidadeTest {

    private BeneficiosConcedidos beneficiosPara(CategoriaFidelidade categoria, int diarias) {
        ContextoFidelidade ctx = new ContextoFidelidade(categoria.getHospedagensMinimas(), 1000.0, diarias);
        return FidelidadeFactory.criarBeneficios(categoria).calcular(ctx);
    }

    @Test
    @DisplayName("Categoria é resolvida pelo número de hospedagens")
    void resolucaoCategoria() {
        assertEquals(CategoriaFidelidade.BRONZE, CategoriaFidelidade.porHospedagens(0));
        assertEquals(CategoriaFidelidade.BRONZE, CategoriaFidelidade.porHospedagens(4));
        assertEquals(CategoriaFidelidade.PRATA, CategoriaFidelidade.porHospedagens(5));
        assertEquals(CategoriaFidelidade.OURO, CategoriaFidelidade.porHospedagens(10));
        assertEquals(CategoriaFidelidade.OURO, CategoriaFidelidade.porHospedagens(19));
        assertEquals(CategoriaFidelidade.DIAMANTE, CategoriaFidelidade.porHospedagens(20));
        assertEquals(CategoriaFidelidade.DIAMANTE, CategoriaFidelidade.porHospedagens(150));
    }

    @Test
    @DisplayName("BRONZE não possui benefícios")
    void bronzeSemBeneficios() {
        BeneficiosConcedidos b = beneficiosPara(CategoriaFidelidade.BRONZE, 3);
        assertEquals(0.0, b.getPercentualDesconto(), 0.001);
        assertFalse(b.isUpgradeQuarto());
        assertEquals(0, b.getHorasCheckoutEstendido());
        assertEquals(0, b.getDiariasGratuitas());
        assertTrue(b.getDescricoes().isEmpty());
    }

    @Test
    @DisplayName("PRATA concede 5% de desconto")
    void prata() {
        BeneficiosConcedidos b = beneficiosPara(CategoriaFidelidade.PRATA, 3);
        assertEquals(0.05, b.getPercentualDesconto(), 0.001);
        assertEquals(1, b.getDescricoes().size());
    }

    @Test
    @DisplayName("OURO empilha desconto de 10% e check-out estendido de 2h")
    void ouro() {
        BeneficiosConcedidos b = beneficiosPara(CategoriaFidelidade.OURO, 3);
        assertEquals(0.10, b.getPercentualDesconto(), 0.001);
        assertEquals(2, b.getHorasCheckoutEstendido());
        assertFalse(b.isUpgradeQuarto());
    }

    @Test
    @DisplayName("DIAMANTE acumula todos os benefícios (Decorator empilhado)")
    void diamante() {
        BeneficiosConcedidos b = beneficiosPara(CategoriaFidelidade.DIAMANTE, 3);
        assertEquals(0.15, b.getPercentualDesconto(), 0.001);
        assertEquals(4, b.getHorasCheckoutEstendido());
        assertTrue(b.isUpgradeQuarto());
        assertEquals(1, b.getDiariasGratuitas());
        assertEquals(4, b.getDescricoes().size());
    }

    @Test
    @DisplayName("Diária gratuita é limitada ao número de diárias da estadia")
    void diariaGratuitaLimitada() {
        // Estadia de 0 diária não deve conceder diária gratuita.
        BeneficiosConcedidos b = beneficiosPara(CategoriaFidelidade.DIAMANTE, 0);
        assertEquals(0, b.getDiariasGratuitas());
    }

    @Test
    @DisplayName("Descontos empilhados acumulam de forma composta")
    void descontosCompostos() {
        // Dois decoradores de desconto: 10% e depois 20% => 1-(0.9*0.8)=0.28
        CalculoBeneficios calculo = new DescontoProgressivoDecorator(
                new DescontoProgressivoDecorator(new SemBeneficios(), 0.10), 0.20);
        BeneficiosConcedidos b = calculo.calcular(new ContextoFidelidade(0, 0.0, 1));
        assertEquals(0.28, b.getPercentualDesconto(), 0.001);
    }
}
