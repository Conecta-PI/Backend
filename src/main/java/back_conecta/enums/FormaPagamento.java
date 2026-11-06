package back_conecta.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum FormaPagamento {
    PIX("pix"),
    DINHEIRO("dinheiro"),
    CARTAO("cartao"),
    FIADO("fiado"),
    AGENDAR("agendar");

    private final String valor;

    FormaPagamento(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static FormaPagamento fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalizado = value.trim().toLowerCase(Locale.ROOT);
        return switch (normalizado) {
            case "pix" -> PIX;
            case "dinheiro" -> DINHEIRO;
            case "cartao", "cartão" -> CARTAO;
            case "fiado" -> FIADO;
            case "agendar" -> AGENDAR;
            default -> FormaPagamento.valueOf(normalizado.toUpperCase(Locale.ROOT));
        };
    }
}