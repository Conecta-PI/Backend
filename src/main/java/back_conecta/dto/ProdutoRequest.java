package back_conecta.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin("0.0") BigDecimal precoCusto,
        @NotNull @DecimalMin(value = "0.01") BigDecimal preco,
        @NotBlank String categoria,
        @NotNull @PositiveOrZero Integer estoque
) {
}
