package back_conecta.validation;

import java.nio.charset.StandardCharsets;

public final class SenhaValidator {
    public static final String REGEX = "(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,72}";

    private SenhaValidator() {
    }

    public static boolean atendeRegras(String senha) {
        if (senha == null || senha.length() < 8 || senha.length() > 72) {
            return false;
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            return false;
        }
        return senha.matches(REGEX);
    }
}
