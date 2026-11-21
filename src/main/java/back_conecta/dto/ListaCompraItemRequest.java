package back_conecta.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ListaCompraItemRequest(
        @NotBlank(message = "Nome do produto é obrigatório")
        String nomeProduto,
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade,
        @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
        BigDecimal precoUnitario,
        Boolean concluido
) {
}
