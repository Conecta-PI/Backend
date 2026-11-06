package back_conecta.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum StatusVenda {
    PENDENTE("pendente"),
    CONCLUIDA("pago"),
    CANCELADA("cancelado");

    private final String valor;

    StatusVenda(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static StatusVenda fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalizado = value.trim().toLowerCase(Locale.ROOT);
        return switch (normalizado) {
            case "pago" -> CONCLUIDA;
            case "pendente" -> PENDENTE;
            case "cancelado", "cancelada" -> CANCELADA;
            default -> StatusVenda.valueOf(normalizado.toUpperCase(Locale.ROOT));
        };
    }
}