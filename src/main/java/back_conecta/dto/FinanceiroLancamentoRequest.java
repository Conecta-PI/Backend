
package back_conecta.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record FinanceiroLancamentoRequest(
        @NotBlank @Pattern(regexp = "entrada|saida", message = "Tipo deve ser entrada ou saida") String tipo,
        @NotNull @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") BigDecimal valor,
        @NotBlank String categoria,
        String descricao,
        Long vendaId
) {}
