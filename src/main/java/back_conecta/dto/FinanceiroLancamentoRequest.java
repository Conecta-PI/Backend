
package back_conecta.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FinanceiroLancamentoRequest(
        @NotBlank String tipo,
        @NotNull @DecimalMin(value = "0.01") BigDecimal valor,
        @NotBlank String categoria,
        String descricao,
        Long vendaId
) {}
