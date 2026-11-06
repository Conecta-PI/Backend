package back_conecta.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LISTA_COMPRAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListaCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista_compra")
    private Long idListaCompra;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 30)
    private String prioridade;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "total_estimado", precision = 12, scale = 2)
    private BigDecimal totalEstimado;

    @ElementCollection
    @CollectionTable(name = "LISTA_COMPRA_ITENS", joinColumns = @JoinColumn(name = "id_lista_compra"))
    private List<ListaCompraItem> itens = new ArrayList<>();

    @PrePersist
    protected void aplicarValoresPadrao() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "aberta";
        }
        if (prioridade == null || prioridade.isBlank()) {
            prioridade = "media";
        }
        if (totalEstimado == null) {
            totalEstimado = BigDecimal.ZERO;
        }
    }
}
