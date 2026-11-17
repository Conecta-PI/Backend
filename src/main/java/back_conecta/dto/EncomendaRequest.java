package back_conecta.dto;

import java.math.BigDecimal;
import java.util.List;

public record EncomendaRequest(
        Long clienteId,
        List<EncomendaItemRequest> itens,
        String dataEntrega,
        String horario,
        String tipoEntrega,
        BigDecimal sinalPago,
        String observacoes
) {
}