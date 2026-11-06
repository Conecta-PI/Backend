package back_conecta.dto;

import back_conecta.entities.ItemVenda;
import java.math.BigDecimal;

public record ItemVendaDetalheResponse(
        Long id,
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
    public static ItemVendaDetalheResponse from(ItemVenda item) {
        return new ItemVendaDetalheResponse(
                item.getIdItem(),
                item.getProduto().getIdProduto(),
                item.getProduto().getNomeProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
        );
    }
}
