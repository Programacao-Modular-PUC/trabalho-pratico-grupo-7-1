package com.marau.hospedagem.model.fidelidade;

/**
 * Categorias do programa de fidelidade, determinadas pelo número de
 * hospedagens já realizadas pelo cliente.
 *
 * <p>Cada categoria define apenas seu limiar de entrada. Os benefícios
 * concretos são montados pela {@code FidelidadeFactory} usando o padrão
 * Decorator, de modo que adicionar um benefício a uma categoria não exige
 * alterar este enum.</p>
 */
public enum CategoriaFidelidade {

    BRONZE(0),
    PRATA(5),
    OURO(10),
    DIAMANTE(20);

    private final int hospedagensMinimas;

    CategoriaFidelidade(int hospedagensMinimas) {
        this.hospedagensMinimas = hospedagensMinimas;
    }

    public int getHospedagensMinimas() {
        return hospedagensMinimas;
    }

    /**
     * Resolve a categoria correspondente a uma quantidade de hospedagens,
     * escolhendo a maior categoria cujo limiar é atingido.
     */
    public static CategoriaFidelidade porHospedagens(int totalHospedagens) {
        CategoriaFidelidade resultado = BRONZE;
        for (CategoriaFidelidade categoria : values()) {
            if (totalHospedagens >= categoria.hospedagensMinimas) {
                resultado = categoria;
            }
        }
        return resultado;
    }
}
