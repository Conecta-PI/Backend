package back_conecta.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "id_cliente")
    private Long clienteId;

    private String status;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(name = "tipo_entrega", length = 20)
    private String tipoEntrega;

    @Column(name = "sinal_pago", precision = 12, scale = 2)
    private BigDecimal sinalPago;

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ElementCollection
    @CollectionTable(
            name = "ENCOMENDA_ITENS",
            joinColumns = @JoinColumn(name = "id_encomenda")
    )
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

        if (sinalPago == null) {
            sinalPago = BigDecimal.ZERO;
        }
    }
}