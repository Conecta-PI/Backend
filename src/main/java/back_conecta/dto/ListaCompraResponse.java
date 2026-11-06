package back_conecta.dto;

import back_conecta.entities.ListaCompra;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ListaCompraResponse(
        Long id,
        String titulo,
        String status,
        String prioridade,
        LocalDateTime dataCriacao,
        LocalDateTime dataConclusao,
        BigDecimal totalEstimado,
        List<ListaCompraItemResponse> itens
) {
    public static ListaCompraResponse from(ListaCompra listaCompra) {
        return new ListaCompraResponse(
                listaCompra.getIdListaCompra(),
                listaCompra.getTitulo(),
                listaCompra.getStatus(),
                listaCompra.getPrioridade(),
                listaCompra.getDataCriacao(),
                listaCompra.getDataConclusao(),
                listaCompra.getTotalEstimado(),
                listaCompra.getItens() == null
                        ? List.of()
                        : listaCompra.getItens().stream().map(ListaCompraItemResponse::from).toList()
        );
    }
}
