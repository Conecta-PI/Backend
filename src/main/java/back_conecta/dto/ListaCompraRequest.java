package back_conecta.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record ListaCompraRequest(
        String titulo,
        @Pattern(regexp = "aberta|concluida", message = "Status da lista inválido")
        String status,
        @Pattern(regexp = "alta|media|baixa", message = "Prioridade da lista inválida")
        String prioridade,
        BigDecimal totalEstimado,
        @Valid
        List<ListaCompraItemRequest> itens
) {
}
