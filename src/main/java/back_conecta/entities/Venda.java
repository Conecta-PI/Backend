package back_conecta.entities;

import back_conecta.enums.FormaPagamento;
import back_conecta.enums.StatusVenda;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "VENDAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idVenda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private Long idVenda;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status_venda", nullable = false)
    private StatusVenda statusVenda;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<ItemVenda> itens;

    @PrePersist
    protected void onCreate() {
        this.dataVenda = LocalDateTime.now();
    }
}