package back_conecta.dto;

import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;

public record VendaUpdateRequest(
        StatusVenda status,
        FormaPagamento formaPagamento
) {
}
