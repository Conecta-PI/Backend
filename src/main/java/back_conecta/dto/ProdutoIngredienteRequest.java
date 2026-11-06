package back_conecta.dto;

public record ProdutoIngredienteRequest(
        String nome,
        Integer quantidade,
        String unidade
) {
}
