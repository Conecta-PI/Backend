package back_conecta.dto;

import back_conecta.entities.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String categoria,
        Integer estoque
) {
    public static ProdutoResponse from(Produto produto) {
        return new ProdutoResponse(
                produto.getIdProduto(),
                produto.getNomeProduto(),
                produto.getDescricao(),
                produto.getPrecoVenda(),
                produto.getCategoria(),
                produto.getEstoque() == null ? 0 : produto.getEstoque()
        );
    }
}
