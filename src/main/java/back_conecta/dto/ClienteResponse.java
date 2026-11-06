package back_conecta.dto;

import back_conecta.entities.Cliente;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        BigDecimal totalGasto,
        Integer quantidadePedidos,
        LocalDateTime ultimoPedido,
        Boolean temPendencia
) {
    public static ClienteResponse from(
            Cliente cliente,
            BigDecimal totalGasto,
            int quantidadePedidos,
            LocalDateTime ultimoPedido,
            boolean temPendencia
    ) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                totalGasto,
                quantidadePedidos,
                ultimoPedido,
                temPendencia
        );
    }
}
