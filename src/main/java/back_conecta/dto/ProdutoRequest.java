package back_conecta.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin(value = "0.01") @JsonProperty("preco") BigDecimal preco,
        @JsonAlias({"precoCusto", "custo"}) BigDecimal precoCusto,
        @NotBlank String categoria,
        @PositiveOrZero @JsonAlias({"quantidadeEstoque", "estoqueDisponivel"}) Integer estoque,
        @JsonAlias({"imagem", "foto"}) String foto,
        String preparo,
        String rendimento,
        List<ProdutoIngredienteRequest> ingredientes,
        Boolean ativo
) {
}
