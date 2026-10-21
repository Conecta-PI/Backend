package back_conecta.entities;

import back_conecta.enums.TipoRegistro;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PREFERENCIAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPreferencia")
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preferencia")
    private Long idPreferencia;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "tipo_registro", nullable = false)
    private TipoRegistro tipoRegistro;

    @Column(nullable = false, length = 30)
    private String canal;

    @Column(nullable = false, length = 500)
    private String descricao;
    
    @PrePersist
    protected void onCreate() {
        this.dataRegistro = LocalDateTime.now();
    }
}