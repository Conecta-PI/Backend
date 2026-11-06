package back_conecta.dto;

import back_conecta.entities.EncomendaItem;
import java.math.BigDecimal;

public record EncomendaItemResponse(String nomeProduto, Integer quantidade, BigDecimal precoUnitario) {
    public static EncomendaItemResponse from(EncomendaItem item) {
        return new EncomendaItemResponse(item.getNomeProduto(), item.getQuantidade(), item.getPrecoUnitario());
    }
}
