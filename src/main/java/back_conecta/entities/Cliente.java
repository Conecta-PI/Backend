package back_conecta.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idCliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private java.util.List<Venda> vendas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private java.util.List<Preferencia> preferencias;
}