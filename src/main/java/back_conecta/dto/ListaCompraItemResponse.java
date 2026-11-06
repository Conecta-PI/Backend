package back_conecta.dto;

import back_conecta.entities.ListaCompraItem;
import java.math.BigDecimal;

public record ListaCompraItemResponse(String nomeProduto, Integer quantidade, BigDecimal precoUnitario, Boolean concluido) {
    public static ListaCompraItemResponse from(ListaCompraItem item) {
        return new ListaCompraItemResponse(item.getNomeProduto(), item.getQuantidade(), item.getPrecoUnitario(), item.getConcluido());
    }
}
