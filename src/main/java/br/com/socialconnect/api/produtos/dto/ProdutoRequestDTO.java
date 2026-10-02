package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.UnidadeMedidaValida;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de ENTRADA para POST e PUT
@Schema(description = "Dados para cadastrar ou substituir um produto do estoque")
public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto (único)", example = "Arroz 5kg")
        @NotBlank(message = "{produto.nome.obrigatorio}")
        @Size(max = 150, message = "{produto.nome.tamanho}")
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "{produto.categoria.obrigatoria}")
        CategoriaProduto categoria,

        // Sem @Min de propósito: estoque negativo é regra de negócio (422), validada no service
        @Schema(description = "Quantidade atual em estoque (não pode ser negativa)", example = "3")
        @NotNull(message = "{produto.estoqueAtual.obrigatorio}")
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima desejada em estoque", example = "10")
        @NotNull(message = "{produto.estoqueMinimo.obrigatorio}")
        @Min(value = 0, message = "{produto.estoqueMinimo.minimo}")
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida", example = "unidade",
                allowableValues = {"unidade", "kg", "g", "l", "ml", "pacote", "caixa", "fardo", "par"})
        @NotBlank(message = "{produto.unidadeMedida.obrigatoria}")
        @Size(max = 20, message = "{produto.unidadeMedida.tamanho}")
        @UnidadeMedidaValida
        String unidadeMedida
) {}
