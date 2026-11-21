package back_conecta.dto;

import back_conecta.entities.EncomendaItem;
import java.math.BigDecimal;

public record EncomendaItemResponse(
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
    public static EncomendaItemResponse from(EncomendaItem item) {
        BigDecimal subtotal = item.getPrecoUnitario() == null || item.getQuantidade() == null
                ? BigDecimal.ZERO
                : item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
        return new EncomendaItemResponse(
                item.getNomeProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal
        );
    }
}
