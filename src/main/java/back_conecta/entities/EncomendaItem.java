package back_conecta.entities;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncomendaItem {
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
