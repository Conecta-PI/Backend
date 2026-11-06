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
@Table(name = "ENCOMENDAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encomenda")
    private Long idEncomenda;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "cliente_nome", nullable = false, length = 120)
    private String clienteNome;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ElementCollection
    @CollectionTable(name = "ENCOMENDA_ITENS", joinColumns = @JoinColumn(name = "id_encomenda"))
    private List<EncomendaItem> itens = new ArrayList<>();

    @PrePersist
    protected void aplicarValoresPadrao() {
        if (dataPedido == null) {
            dataPedido = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "pendente";
        }
        if (valorTotal == null) {
            valorTotal = BigDecimal.ZERO;
        }
    }
}
