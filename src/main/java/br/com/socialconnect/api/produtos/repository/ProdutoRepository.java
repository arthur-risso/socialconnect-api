package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    boolean existsByNomeIgnoreCase(String nome);

    // Usado no PUT: o próprio produto pode manter o nome
    boolean existsByNomeIgnoreCaseAndIdProdutoNot(String nome, Long idProduto);
}
