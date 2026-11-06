package back_conecta.dto;

import java.math.BigDecimal;

public record EncomendaItemRequest(String nomeProduto, Integer quantidade, BigDecimal precoUnitario) {
}
