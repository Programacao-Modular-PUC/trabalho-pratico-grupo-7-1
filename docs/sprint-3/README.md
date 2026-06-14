# Sprint 3 - Tratamento de Exceções e Testes Unitários

Adaptação do sistema para tratar e lançar exceções personalizadas e implementação
de testes unitários com **JUnit 5**, além dos novos requisitos da sprint.

> **Importante (infraestrutura):** nesta sprint o projeto foi reorganizado para o
> layout **Maven padrão** (`src/main/java`, `src/test/java`) e ganhou `pom.xml`,
> classe principal `HospedagemApplication` e **Maven Wrapper** (`mvnw`), para que
> os testes possam ser compilados, executados e o relatório gerado.

---

## 1. Exceções personalizadas

Todas herdam de `HospedagemException` (que estende `RuntimeException`), em
`com.marau.hospedagem.exception`:

| Exceção | Lançada quando... | Onde |
|---------|-------------------|------|
| `QuartoIndisponivelException` | o quarto não está LIVRE no período | `AluguelService.criar` |
| `CapacidadeExcedidaException` | nº de hóspedes > capacidade do quarto | `Aluguel` (construtor), `QuartoFamilia.calcularComHospedes` |
| `DataInvalidaException` | datas nulas ou saída ≤ entrada | `Aluguel` (construtor) |
| `RecursoNaoPermitidoException` | berço solicitado em quarto que não suporta | `Quarto.solicitarBerco` (Individual/Família) |
| `EntidadeNaoEncontradaException` *(apoio)* | entidade não encontrada por id/cpf | todos os *services* |

### Berço via polimorfismo
A regra "berço só em quarto duplo" foi modelada de forma orientada a objetos:

- `Quarto.solicitarBerco()` (classe base) **lança** `RecursoNaoPermitidoException`;
- `QuartoDuplo` **sobrescreve** o método para de fato habilitar o berço.

Assim, `AluguelService` apenas chama `quarto.solicitarBerco()` e o tipo correto
decide o comportamento.

### Tratamento centralizado (incl. exceções do Java)
`GlobalExceptionHandler` (`@RestControllerAdvice`) converte cada exceção em uma
resposta HTTP padronizada (`ProblemDetail`):

| Exceção | HTTP |
|---------|------|
| `EntidadeNaoEncontradaException` | 404 Not Found |
| `QuartoIndisponivelException` | 409 Conflict |
| `CapacidadeExcedidaException`, `RecursoNaoPermitidoException` | 422 Unprocessable Entity |
| `DataInvalidaException`, `IllegalArgumentException` | 400 Bad Request |
| `Exception` (genérica do Java) | 500 Internal Server Error |

---

## 2. Testes com JUnit

**44 testes, 0 falhas** — relatório completo em
[`relatorio-testes/`](relatorio-testes/) ([RESUMO.md](relatorio-testes/RESUMO.md)).

Itens exigidos pelo enunciado e onde são testados:

- **Cálculo de diária por tipo de quarto** → `QuartoIndividualTest`, `QuartoDuploTest`, `QuartoFamiliaTest`
- **Regras de berço** → `BercoRegraTest`, `QuartoDuploTest`
- **Limites de hóspedes** → `QuartoFamiliaTest`, `AluguelTest`
- **Disponibilidade** → `AluguelServiceTest`

Os testes de *service* usam **Mockito** (sem banco de dados); os de modelo são
testes unitários puros do domínio.

---

## 3. Novos requisitos

| Requisito | Implementação | Teste |
|-----------|---------------|-------|
| **Filtro por tipo de quarto** | `QuartoService.listarPorTipo(residenciaId, tipo)` | `QuartoServiceTest` |
| **Cancelamento de aluguel** | `AluguelService.cancelar(id)` + `Aluguel.cancelar()` (libera o quarto e cancela o pagamento) | `AluguelServiceTest`, `AluguelTest` |
| **Histórico por cliente** | `AluguelService.historicoPorCliente(clienteId)` | `AluguelServiceTest` |

---

## 4. Como executar os testes

Requer **JDK 17+** (o projeto foi validado com JDK 21). Não é preciso instalar o
Maven — use o wrapper:

```bash
# Windows
mvnw.cmd test

# Linux/macOS
./mvnw test
```

Gerar o relatório em HTML (após os testes):

```bash
./mvnw surefire-report:report-only
# Saída: target/reports/surefire.html
```

Os relatórios brutos do JUnit ficam em `target/surefire-reports/` (XML e TXT) e
foram copiados para [`relatorio-testes/`](relatorio-testes/) como entrega desta sprint.
