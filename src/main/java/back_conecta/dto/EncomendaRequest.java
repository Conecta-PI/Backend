package back_conecta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EncomendaRequest(
        String codigo,
        String clienteNome,
        String status,
        LocalDateTime dataEntrega,
        BigDecimal valorTotal,
        String observacao,
        List<EncomendaItemRequest> itens
) {
}
