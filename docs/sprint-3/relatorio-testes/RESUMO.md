# Relatório de Testes - Sprint 3 (JUnit 5 / Surefire)

**Resultado geral:** `Tests run: 44, Failures: 0, Errors: 0, Skipped: 0` — **BUILD SUCCESS** ✅

Gerado com **Maven Surefire** sobre **JUnit 5 (Jupiter)** e **Mockito**.
Arquivos brutos do JUnit nesta pasta: [surefire.html](surefire.html) (HTML) e
[surefire-reports/](surefire-reports/) (XML + TXT por classe).

> Para regerar: `./mvnw test` e, opcionalmente, `./mvnw surefire-report:report-only`
> (o HTML é gravado em `target/reports/surefire.html`).

## Cobertura por classe

| Classe | Testes | Foco (requisito da Sprint 3) |
|--------|:-----:|------------------------------|
| `model/QuartoIndividualTest` | 5 | Cálculo de diária por tipo de quarto |
| `model/QuartoDuploTest` | 5 | Diária + regras de berço |
| `model/QuartoFamiliaTest` | 7 | Diária por hóspedes, descontos e limites |
| `model/AluguelTest` | 9 | Diárias (regra 12h), datas, capacidade, status |
| `model/BercoRegraTest` | 3 | Regras de berço (RecursoNaoPermitido) |
| `service/AluguelServiceTest` | 7 | Disponibilidade, exceções, cancelamento, histórico |
| `service/QuartoServiceTest` | 8 | Filtro por tipo + criação (exceções do Java) |
| **Total** | **44** | |

## Detalhamento dos casos de teste

### `QuartoIndividualTest` (cálculo de diária e limites)
- ✅ Diária com 1 cama é igual ao valor base
- ✅ Diária soma o adicional de ar-condicionado quando habilitado
- ✅ Diária soma adicional por cama extra a partir da 2ª cama
- ✅ Capacidade máxima é igual ao número de camas
- ✅ Tipo do quarto é INDIVIDUAL

### `QuartoDuploTest` (diária e berço)
- ✅ Diária = base + adicional de conforto (sem berço)
- ✅ Diária soma a taxa de berço quando solicitado
- ✅ Diária soma ar, hidromassagem e conforto
- ✅ Capacidade é 2 sem berço e 3 com berço
- ✅ Remover berço volta a capacidade para 2

### `QuartoFamiliaTest` (diária por hóspedes e descontos)
- ✅ Capacidade = solteiro + 2×casal + 2×queen/king
- ✅ Diária com 1 hóspede aplica o percentual por hóspede
- ✅ 5 hóspedes aplicam 10% de desconto
- ✅ 7 hóspedes aplicam 15% de desconto
- ✅ 10 hóspedes aplicam 20% de desconto
- ✅ Faixas de desconto progressivo por número de hóspedes
- ✅ Exceder a capacidade lança `CapacidadeExcedidaException`

### `AluguelTest` (diárias, validações e status)
- ✅ Calcula 2 diárias quando saída ocorre às 12h (sem diária extra)
- ✅ Saída após as 12h adiciona uma diária extra
- ✅ Data de saída anterior à entrada lança `DataInvalidaException`
- ✅ Datas nulas lançam `DataInvalidaException`
- ✅ Nº de hóspedes acima da capacidade lança `CapacidadeExcedidaException`
- ✅ Fluxo de status: RESERVADO → ATIVO → ENCERRADO
- ✅ Cancelar um aluguel reservado o coloca como CANCELADO
- ✅ Encerrar um aluguel não ativo lança `IllegalStateException`
- ✅ Cancelar um aluguel já encerrado lança `IllegalStateException`

### `BercoRegraTest` (recurso não permitido)
- ✅ Berço em quarto INDIVIDUAL não é permitido
- ✅ Berço em quarto FAMÍLIA não é permitido
- ✅ Berço em quarto DUPLO é permitido e aumenta a capacidade

### `AluguelServiceTest` (disponibilidade, exceções e novos requisitos)
- ✅ Criar aluguel com quarto livre reserva o quarto e calcula diárias
- ✅ Criar aluguel com quarto ocupado lança `QuartoIndisponivelException`
- ✅ Solicitar berço em quarto individual lança `RecursoNaoPermitidoException`
- ✅ Residência inexistente lança `EntidadeNaoEncontradaException`
- ✅ Cancelar aluguel libera o quarto e cancela o pagamento
- ✅ Histórico por cliente retorna os aluguéis do cliente
- ✅ Histórico de cliente inexistente lança `EntidadeNaoEncontradaException`

### `QuartoServiceTest` (filtro por tipo e criação)
- ✅ Filtra apenas os quartos do tipo informado
- ✅ Filtro por tipo é case-insensitive
- ✅ Tipo nulo (no filtro) lança `IllegalArgumentException`
- ✅ Listar disponíveis delega ao repositório filtrando por status LIVRE
- ✅ Criar quarto duplo com sucesso adiciona o quarto à residência
- ✅ Criar quarto com tipo nulo lança `IllegalArgumentException` (trata NPE do Java)
- ✅ Criar quarto com tipo inválido lança `IllegalArgumentException`
- ✅ Criar quarto duplo com tipo de cama inválido lança `IllegalArgumentException`
