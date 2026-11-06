package back_conecta.dto;

import back_conecta.enums.StatusVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ClienteResumoResponse(
        Long id,
        String nome,
        BigDecimal totalGasto,
        Integer quantidadePedidos,
        LocalDateTime ultimoPedido,
        Boolean temPendencia,
        List<PedidoResumo> pedidos
) {
    public record PedidoResumo(Long id, LocalDateTime data, BigDecimal valor, StatusVenda status) {
    }
}
