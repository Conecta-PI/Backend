package back_conecta.dto;

import back_conecta.entities.Venda;
import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(
        Long id,
        String cliente,
        LocalDateTime data,
        BigDecimal total,
        StatusVenda status,
        FormaPagamento formaPagamento,
        List<ItemResumo> itens
) {
    public record ItemResumo(String nome, Integer quantidade) {
    }

    public static VendaResponse from(Venda venda) {
        List<ItemResumo> itens = venda.getItens() == null
                ? List.of()
                : venda.getItens().stream()
                        .map(item -> new ItemResumo(item.getProduto().getNomeProduto(), item.getQuantidade()))
                        .toList();

        return new VendaResponse(
                venda.getIdVenda(),
                venda.getCliente().getNome(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getStatusVenda(),
                venda.getFormaPagamento(),
                itens
        );
    }
}
