package back_conecta.dto;

import back_conecta.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record VendaRequest(
        @NotNull Long clienteId,
        @NotNull FormaPagamento formaPagamento,
        @NotEmpty List<@Valid ItemVendaRequest> itens
) {
}
