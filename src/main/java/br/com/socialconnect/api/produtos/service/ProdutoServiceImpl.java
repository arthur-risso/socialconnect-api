package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.produtos.repository.ProdutoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        return produtoRepository
                .findAll(ProdutoSpecifications.filtrar(nome, categoria), pageable)
                .map(this::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    // POST
    @Override
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoqueNaoNegativo(dto.estoqueAtual());
        String nome = dto.nome().trim();
        if (produtoRepository.existsByNomeIgnoreCase(nome)) {
            throw new NomeProdutoDuplicadoException(nome);
        }
        Produto produto = Produto.builder()
                .nome(nome)
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida().trim())
                .dataCadastro(LocalDate.now())
                .build();
        return toResponseDTO(produtoRepository.save(produto));
    }

    // PUT (substituição total; dataCadastro é preservada)
    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscarEntidade(id);
        validarEstoqueNaoNegativo(dto.estoqueAtual());
        String nome = dto.nome().trim();
        if (produtoRepository.existsByNomeIgnoreCaseAndIdProdutoNot(nome, id)) {
            throw new NomeProdutoDuplicadoException(nome);
        }
        produto.setNome(nome);
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida().trim());
        return toResponseDTO(produtoRepository.save(produto));
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // Regra de negócio: nenhuma operação pode resultar em estoque negativo
    private void validarEstoqueNaoNegativo(Integer estoqueAtual) {
        if (estoqueAtual < 0) {
            throw new EstoqueNegativoException(estoqueAtual);
        }
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private ProdutoResponseDTO toResponseDTO(Produto p) {
        return new ProdutoResponseDTO(
                p.getIdProduto(),
                p.getNome(),
                p.getCategoria(),
                p.getEstoqueAtual(),
                p.getEstoqueMinimo(),
                p.getUnidadeMedida(),
                p.getDataCadastro(),
                p.getEstoqueAtual() < p.getEstoqueMinimo()
        );
    }
}
