package back_conecta.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MOVIMENTACOES_FINANCEIRAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idMovimentacao")
public class MovimentacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Long idMovimentacao;

    @Column(name = "tipo_movimento", nullable = false)
    private Boolean tipoMovimento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "categoria_saida", length = 50)
    private String categoriaSaida;

    @Column(name = "data_movimento", nullable = false)
    private LocalDateTime dataMovimento;

    @ManyToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    @PrePersist
    protected void onCreate() {
        this.dataMovimento = LocalDateTime.now();
    }
}