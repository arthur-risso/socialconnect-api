# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** ITE005 — Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · H2 (dev) · PostgreSQL (prod)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd socialconnect-api

# 2. Compile o projeto
mvn clean compile

# 3. Rode a aplicação
mvn spring-boot:run

# 4. Rode os testes (integração exige Docker ativo — PostgreSQL 17 via Testcontainers)
mvn test
```

### Endereços úteis (perfil de desenvolvimento)

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/api-docs |
| Console H2 | http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:socialconnectdb`) |

---

## Endpoints Principais

Todos os erros seguem o formato **Problem Details (RFC 7807)**.

### Beneficiários — `/api/v1/beneficiarios`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/beneficiarios` | Lista paginada (filtros `nome`, `cpf`) |
| GET | `/api/v1/beneficiarios/{idBeneficiario}` | Busca por ID |
| POST | `/api/v1/beneficiarios` | Cadastra (409 se CPF duplicado) |
| PUT / PATCH | `/api/v1/beneficiarios/{idBeneficiario}` | Atualiza total / parcialmente |
| DELETE | `/api/v1/beneficiarios/{idBeneficiario}` | Remove |

### Doações — `/api/v1/doacoes`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/v1/doacoes` | Lista paginada (filtros `dataInicio`, `dataFim`, `tipo`) |
| GET | `/api/v1/doacoes/{idDoacao}` | Busca por ID |
| POST | `/api/v1/doacoes` | Registra doação |
| PUT / PATCH | `/api/v1/doacoes/{idDoacao}` | Atualiza total / parcialmente |
| DELETE | `/api/v1/doacoes/{idDoacao}` | Remove |

### Produtos (estoque) — `/api/v1/produtos` · Avaliação A1

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| GET | `/api/v1/produtos?page=0&size=10&sort=nome,asc&nome=arroz&categoria=ALIMENTO` | 200 | 400 |
| GET | `/api/v1/produtos/{idProduto}` | 200 | 404 |
| POST | `/api/v1/produtos` | 201 + `Location` | 400, 409, 422 |
| PUT | `/api/v1/produtos/{idProduto}` | 200 | 400, 404, 409, 422 |
| DELETE | `/api/v1/produtos/{idProduto}` | 204 | 404 |

**Regras de negócio:**

- **Estoque não negativo:** `estoqueAtual < 0` é rejeitado com `422 Unprocessable Entity`.
- **Alerta de estoque baixo:** a resposta traz `estoqueBaixo = true` quando `estoqueAtual < estoqueMinimo`.
- **Nome único:** nomes repetidos (sem diferenciar maiúsculas/minúsculas) retornam `409 Conflict`.
- **Unidade de medida:** validação customizada `@UnidadeMedidaValida` aceita `unidade`, `kg`, `g`, `l`, `ml`, `pacote`, `caixa`, `fardo`, `par` (400 se inválida).

Exemplo de requisição (`POST /api/v1/produtos`):

```json
{
  "nome": "Arroz 5kg",
  "categoria": "ALIMENTO",
  "estoqueAtual": 3,
  "estoqueMinimo": 10,
  "unidadeMedida": "unidade"
}
```

Resposta `201 Created`:

```json
{
  "idProduto": 1,
  "nome": "Arroz 5kg",
  "categoria": "ALIMENTO",
  "estoqueAtual": 3,
  "estoqueMinimo": 10,
  "unidadeMedida": "unidade",
  "dataCadastro": "2026-10-02",
  "estoqueBaixo": true
}
```