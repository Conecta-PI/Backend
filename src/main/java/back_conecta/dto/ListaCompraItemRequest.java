package back_conecta.dto;

import java.math.BigDecimal;

public record ListaCompraItemRequest(String nomeProduto, Integer quantidade, BigDecimal precoUnitario, Boolean concluido) {
}
