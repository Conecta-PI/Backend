package back_conecta.dto;

import java.math.BigDecimal;
import java.util.List;

public record ListaCompraRequest(
        String titulo,
        String status,
        String prioridade,
        BigDecimal totalEstimado,
        List<ListaCompraItemRequest> itens
) {
}
