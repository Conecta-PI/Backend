package back_conecta.dto;

import back_conecta.entities.Produto;
import back_conecta.dto.ProdutoIngredienteRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String categoria,
        Integer estoque,
        String foto,
        String preparo,
        String rendimento,
        List<ProdutoIngredienteRequest> ingredientes,
        Boolean ativo
) {
    public static ProdutoResponse from(Produto produto) {
        Objects.requireNonNull(produto, "Produto não pode ser nulo");

        return new ProdutoResponse(
                produto.getIdProduto(),
                produto.getNomeProduto(),
                produto.getDescricao(),
                produto.getPrecoVenda() == null ? BigDecimal.ZERO : produto.getPrecoVenda(),
                produto.getCategoria() == null ? "Geral" : produto.getCategoria(),
                produto.getEstoque() == null ? 0 : produto.getEstoque(),
                produto.getFoto(),
                produto.getPreparo(),
                produto.getRendimento(),
                deserializarIngredientes(produto.getIngredientes()),
                Boolean.TRUE.equals(produto.getAtivo())
        );
    }

    private static List<ProdutoIngredienteRequest> deserializarIngredientes(String ingredientes) {
        if (ingredientes == null || ingredientes.isBlank()) {
            return List.of();
        }
        java.util.regex.Matcher objeto = java.util.regex.Pattern
                .compile("\\{([^{}]*)}")
                .matcher(ingredientes);
        java.util.ArrayList<ProdutoIngredienteRequest> resultado = new java.util.ArrayList<>();
        while (objeto.find()) {
            String corpo = objeto.group(1);
            resultado.add(new ProdutoIngredienteRequest(
                    extrairTexto(corpo, "nome"),
                    extrairInteiro(corpo, "quantidade"),
                    extrairTexto(corpo, "unidade")
            ));
        }
        return resultado;
    }

    private static String extrairTexto(String json, String campo) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\"" + campo + "\"\\s*:\\s*\"([^\"]*)\"")
                .matcher(json);
        return matcher.find() ? matcher.group(1).replace("\\\"", "\"").replace("\\n", "\n") : null;
    }

    private static Integer extrairInteiro(String json, String campo) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\"" + campo + "\"\\s*:\\s*(-?[0-9]+)")
                .matcher(json);
        return matcher.find() ? Integer.valueOf(matcher.group(1)) : null;
    }
}
