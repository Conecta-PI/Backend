package back_conecta.dto;

import back_conecta.entities.Produto;
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
                List.of(),
                Boolean.TRUE.equals(produto.getAtivo())
        );
    }
}
