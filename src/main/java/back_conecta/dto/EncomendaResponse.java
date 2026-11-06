package back_conecta.dto;

import back_conecta.entities.Encomenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EncomendaResponse(
        Long id,
        String codigo,
        String clienteNome,
        String status,
        LocalDateTime dataPedido,
        LocalDateTime dataEntrega,
        BigDecimal valorTotal,
        String observacao,
        List<EncomendaItemResponse> itens
) {
    public static EncomendaResponse from(Encomenda encomenda) {
        return new EncomendaResponse(
                encomenda.getIdEncomenda(),
                encomenda.getCodigo(),
                encomenda.getClienteNome(),
                encomenda.getStatus(),
                encomenda.getDataPedido(),
                encomenda.getDataEntrega(),
                encomenda.getValorTotal(),
                encomenda.getObservacao(),
                encomenda.getItens() == null
                        ? List.of()
                        : encomenda.getItens().stream().map(EncomendaItemResponse::from).toList()
        );
    }
}
