package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestRestTemplate
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProdutoControllerIntegrationTest {

    // PostgreSQL 17 efêmero; @ServiceConnection liga o datasource do Spring a este container
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    // Em testes o Spring injeta os beans no campo; não há construtor para usar aqui
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("POST /api/v1/produtos com dados válidos deve retornar 201 Created")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<ProdutoResponseDTO> resposta =
                restTemplate.postForEntity("/api/v1/produtos", dto, ProdutoResponseDTO.class);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertNotNull(resposta.getBody().idProduto(), "ID do produto não deve ser nulo");
        Assertions.assertTrue(resposta.getBody().estoqueBaixo(), "3 < 10 deve sinalizar estoque baixo");
        Assertions.assertNotNull(resposta.getHeaders().getLocation());
    }

    @Test
    @DisplayName("POST /api/v1/produtos com nome já cadastrado deve retornar 409 com Problem Details")
    void deveRetornar409QuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO original = new ProdutoRequestDTO(
                "Sabonete", CategoriaProduto.HIGIENE, 20, 5, "unidade"
        );
        restTemplate.postForEntity("/api/v1/produtos", original, ProdutoResponseDTO.class);
        ProdutoRequestDTO duplicado = new ProdutoRequestDTO(
                "sabonete", CategoriaProduto.HIGIENE, 8, 5, "unidade"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<String> resposta = restTemplate.postForEntity("/api/v1/produtos", duplicado, String.class);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().contains("\"status\":409"));
        Assertions.assertTrue(resposta.getBody().contains("produto-duplicado"));
    }

    @Test
    @DisplayName("POST /api/v1/produtos com estoque negativo deve retornar 422 com Problem Details")
    void deveRetornar422QuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Camiseta P", CategoriaProduto.ROUPA, -1, 5, "unidade"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<String> resposta = restTemplate.postForEntity("/api/v1/produtos", dto, String.class);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getBody());
        Assertions.assertTrue(resposta.getBody().contains("\"status\":422"));
        Assertions.assertTrue(resposta.getBody().contains("\"field\":\"estoqueAtual\""));
    }
}
