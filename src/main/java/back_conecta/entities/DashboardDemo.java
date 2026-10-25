package back_conecta.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DASHBOARD_DEMO")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DashboardDemo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nomeUsuario;

    @Column(nullable = false, length = 150)
    private String nomeNegocio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal meta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lucro;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal custo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal investido;
}
