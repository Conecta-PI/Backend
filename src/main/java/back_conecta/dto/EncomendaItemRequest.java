package back_conecta.dto;

public record EncomendaItemRequest(
        Long produtoId,
        Integer quantidade
) {
}