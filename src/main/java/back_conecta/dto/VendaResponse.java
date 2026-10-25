package back_conecta.dto;

import back_conecta.entities.Venda;
import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaResponse(
        Long id,
        String cliente,
        LocalDateTime data,
        BigDecimal total,
        StatusVenda status,
        FormaPagamento formaPagamento
) {
    public static VendaResponse from(Venda venda) {
        return new VendaResponse(
                venda.getIdVenda(),
                venda.getCliente().getNome(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getStatusVenda(),
                venda.getFormaPagamento()
        );
    }
}
