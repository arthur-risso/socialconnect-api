package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    @Test
    @DisplayName("Deve criar produto quando dados válidos")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade"
        );
        Produto produtoSalvo = Produto.builder()
                .idProduto(1L).nome(dto.nome()).categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual()).estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida()).dataCadastro(LocalDate.now())
                .build();
        Mockito.when(produtoRepository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(false);
        Mockito.when(produtoRepository.save(Mockito.any())).thenReturn(produtoSalvo);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ProdutoResponseDTO resultado = produtoService.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertNotNull(resultado.idProduto(), "ID do produto não deve ser nulo");
        Assertions.assertEquals("Arroz 5kg", resultado.nome());
        Assertions.assertTrue(resultado.estoqueBaixo(), "3 < 10 deve sinalizar estoque baixo");
        Mockito.verify(produtoRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o estoque atual for negativo")
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Feijão 1kg", CategoriaProduto.ALIMENTO, -5, 10, "kg"
        );

        // ==========================================
        // ACT + ASSERT: a ação é executada dentro do assertThrows
        // ==========================================
        EstoqueNegativoException excecao = Assertions.assertThrows(EstoqueNegativoException.class, () -> {
            produtoService.criar(dto);
        });

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals(-5, excecao.getEstoque());
        Mockito.verify(produtoRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existir produto com o mesmo nome")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Sabonete", CategoriaProduto.HIGIENE, 20, 5, "unidade"
        );
        Mockito.when(produtoRepository.existsByNomeIgnoreCase("Sabonete")).thenReturn(true);

        // ==========================================
        // ACT + ASSERT: a ação é executada dentro do assertThrows
        // ==========================================
        NomeProdutoDuplicadoException excecao = Assertions.assertThrows(NomeProdutoDuplicadoException.class, () -> {
            produtoService.criar(dto);
        });

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertEquals("Sabonete", excecao.getNome());
        Mockito.verify(produtoRepository, Mockito.never()).save(Mockito.any());
    }
}
