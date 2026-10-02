# Registro de Uso de IA Generativa

> **Política da disciplina:** O uso de IA generativa é permitido como
> assistente. O discente é **integralmente responsável** por testar, auditar e
> defender todo o código entregue, independentemente de como foi gerado.

## Instruções

Para cada aula ou entrega, registre abaixo:
- **Data**
- **Ferramenta** (ChatGPT, Copilot, Claude, etc.)
- **Prompt(s) utilizado(s)** (resumo ou cópia)
- **O que foi feito com a saída** (copiado integralmente, adaptado, usado como referência, descartado)

---

## Registro

| Data | Aula | Ferramenta | Prompt (resumo) | Uso da saída |
|------|------|------------|-----------------|--------------|
| 02/10/2026 | Avaliação 1 (Produtos) | Claude | "Analise o projeto e a atividade proposta no arquivo `avaliacao1.html` e descreva as implementações que deseja fazer e o porquê de cada uma" | Usado como plano de trabalho. A IA apontou o conflito entre o `@Min(0)` do DTO de exemplo (que gera 400) e a regra de estoque negativo (que exige 422); decidi manter a regra no service para retornar 422. |
| 02/10/2026 | Avaliação A1 (Produtos) | Claude | "Altere o necessário para que retorne 422" (implementação do módulo de produtos) | Código do módulo `produtos` (Entity, migration V4, DTOs, `@UnidadeMedidaValida`, Service, Controller, handlers 409/422), testes `ProdutoServiceTest` e `ProdutoControllerIntegrationTest` e atualização do README gerados pelo Claude seguindo os padrões já existentes no projeto (módulo de doações); revisados e testados por mim (Swagger e `mvn test`). Commits feitos por mim. |
| 02/10/2026 | Avaliação A1 (Produtos) | Claude | "Os Conventional Commits são os commits padrões já utilizados?" | Usado como referência: explicação do formato `tipo(escopo): descrição` e sugestão de divisão dos commits por etapa (entidade, DTOs, service, controller, testes, docs). Os comandos `git add`/`git commit` foram executados por mim. |
| 02/10/2026 | Avaliação A1 (Produtos) | Claude | "Realize os testes do desafio bônus e traga os resultados." | A IA confirmou os testes do bônus e os executou com o Docker ativo: `ProdutoServiceTest` (3/3) e `ProdutoControllerIntegrationTest` com PostgreSQL 17 via Testcontainers (3/3), todos passando. |

### Declaração de Uso de IA (A1)

**Ferramentas utilizadas:**
- [x] Claude (Claude Code)
- [ ] Copilot / Codeium
- [ ] Nenhuma

**Como utilizei:**
- Usei IA para analisar o enunciado e planejar as implementações.
- Usei IA para gerar o módulo de produtos seguindo o padrão do módulo de doações.
- Usei IA para gerar os testes unitários e de integração no padrão AAA.
- Usei IA para entender o padrão Conventional Commits e organizar meus commits.
- Usei IA para executar e validar os testes do bônus (unitários e de integração com Testcontainers).

**O que eu entendo 100%:**
- A criação dos CRUDs
- Os status HTTP do CRUD
- Entidade e migration
- O campo estoqueBaixo

**O que precisei estudar mais:**
- Por que estoque negativo dá 422 e não 400
- Nome único no PUT
- Testes de integração com Testcontainers

---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._