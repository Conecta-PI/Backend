package back_conecta.dto;

import java.util.List;

public record FichaTecnicaRequest(
        String foto,
        String preparo,
        String rendimento,
        List<ProdutoIngredienteRequest> ingredientes
) {
}
