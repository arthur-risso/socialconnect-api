package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

// Filtros dinâmicos: cada filtro nulo vira "sem restrição"
public final class ProdutoSpecifications {

    private ProdutoSpecifications() {
    }

    public static Specification<Produto> filtrar(String nome, CategoriaProduto categoria) {
        return (root, query, cb) -> {
            var predicado = cb.conjunction();
            if (nome != null && !nome.isBlank()) {
                String padrao = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
                predicado = cb.and(predicado, cb.like(cb.lower(root.get("nome")), padrao));
            }
            if (categoria != null) {
                predicado = cb.and(predicado, cb.equal(root.get("categoria"), categoria));
            }
            return predicado;
        };
    }
}
