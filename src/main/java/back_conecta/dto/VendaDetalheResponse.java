package back_conecta.dto;

import back_conecta.entities.Venda;
import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record VendaDetalheResponse(
        Long id,
        Long clienteId,
        String cliente,
        LocalDateTime data,
        BigDecimal total,
        StatusVenda status,
        FormaPagamento formaPagamento,
        List<ItemVendaDetalheResponse> itens
) {
    public static VendaDetalheResponse from(Venda venda) {
        Objects.requireNonNull(venda, "Venda não pode ser nula");

        var cliente = venda.getCliente();
        return new VendaDetalheResponse(
                venda.getIdVenda(),
                cliente == null ? null : cliente.getIdCliente(),
                cliente == null ? null : cliente.getNome(),
                venda.getDataVenda(),
                venda.getValorTotal() == null ? BigDecimal.ZERO : venda.getValorTotal(),
                venda.getStatusVenda(),
                venda.getFormaPagamento(),
                venda.getItens() == null ? List.of() : venda.getItens().stream()
                        .filter(Objects::nonNull)
                        .map(ItemVendaDetalheResponse::from)
                        .toList()
        );
    }
}
