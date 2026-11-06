package back_conecta.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

public record ProdutoUpdateRequest(
        @NotBlank(message = "Nome do produto é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Preço de venda é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço de venda deve ser maior que zero")
        @JsonProperty("preco")
        BigDecimal preco,

        @JsonAlias({"precoCusto", "custo"})
        BigDecimal precoCusto,

        @NotBlank(message = "Categoria é obrigatória")
        String categoria,

        @NotNull(message = "Estoque é obrigatório")
        @PositiveOrZero(message = "Estoque não pode ser negativo")
        @JsonAlias({"quantidadeEstoque", "estoqueDisponivel"})
        Integer estoque,

        @JsonAlias({"imagem", "foto"})
        String foto,

        String preparo,
        String rendimento,
        List<ProdutoIngredienteRequest> ingredientes,
        Boolean ativo
) {
}
