package back_conecta.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idProduto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @Column(name = "nome_produto", nullable = false, length = 100)
    private String nomeProduto;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "preco_custo", precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(length = 50)
    private String categoria;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer estoque;

    @Column(nullable = false)
    private Boolean ativo;

    @PrePersist
    protected void aplicarValoresPadrao() {
        if (categoria == null || categoria.isBlank()) {
            categoria = "Geral";
        }
        if (estoque == null) {
            estoque = 0;
        }
        if (ativo == null) {
            ativo = true;
        }
    }
}