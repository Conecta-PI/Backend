package back_conecta.services;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilImageStorage {
    private final Path diretorio;
    private final int tamanhoMaximo;

    public PerfilImageStorage(
            @Value("${app.upload.perfil-dir:./uploads/perfil}") String diretorio,
            @Value("${app.upload.perfil-max-bytes:5242880}") int tamanhoMaximo
    ) throws Exception {
        this.diretorio = Path.of(diretorio).toAbsolutePath().normalize();
        this.tamanhoMaximo = tamanhoMaximo;
        Files.createDirectories(this.diretorio);
    }

    public String processar(String foto) {
        if (foto == null) {
            return null;
        }
        String valor = foto.trim();
        if (valor.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
        }
        if (valor.startsWith("http://") || valor.startsWith("https://")
                || valor.startsWith("/uploads/") || valor.startsWith("/images/")
                || (!valor.startsWith("/") && !valor.contains("://") && valor.matches("[^\\n\\r]+"))) {
            return valor;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
    }

    public String salvarDataUrl(String foto) {
        if (foto == null) {
            return null;
        }
        String valor = foto.trim();
        if (!valor.startsWith("data:image/")) {
            return processar(valor);
        }

        int separador = valor.indexOf(',');
        if (separador < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
        }
        String metadados = valor.substring(5, separador).toLowerCase();
        String base64 = valor.substring(separador + 1);
        String extensao;
        if (metadados.contains("image/jpeg")) extensao = "jpg";
        else if (metadados.contains("image/png")) extensao = "png";
        else if (metadados.contains("image/webp")) extensao = "webp";
        else if (metadados.contains("image/gif")) extensao = "gif";
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem não permitido.");

        byte[] dados;
        try {
            dados = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
        }
        if (dados.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
        }
        if (dados.length > tamanhoMaximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil excede o tamanho máximo.");
        }
        if (!assinaturaValida(dados, metadados)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem de perfil inválida.");
        }

        try {
            String nomeArquivo = UUID.randomUUID() + "." + extensao;
            Path destino = diretorio.resolve(nomeArquivo);
            Files.write(destino, dados);
            return "/uploads/perfil/" + nomeArquivo;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar a imagem de perfil.");
        }
    }

    public void removerAntiga(String caminho) {
        if (caminho == null || !caminho.startsWith("/uploads/perfil/")) {
            return;
        }
        String nome = Path.of(caminho).getFileName().toString();
        try {
            Files.deleteIfExists(diretorio.resolve(nome));
        } catch (Exception ignored) {
        }
    }

    private boolean assinaturaValida(byte[] dados, String metadados) {
        if (metadados.contains("image/jpeg")) {
            return dados.length >= 3 && dados[0] == (byte) 0xff && dados[1] == (byte) 0xd8 && dados[2] == (byte) 0xff;
        }
        if (metadados.contains("image/png")) {
            return dados.length >= 8
                    && dados[0] == (byte) 0x89 && dados[1] == 0x50 && dados[2] == 0x4e && dados[3] == 0x47
                    && dados[4] == 0x0d && dados[5] == 0x0a && dados[6] == 0x1a && dados[7] == 0x0a;
        }
        if (metadados.contains("image/gif")) {
            String assinatura = new String(dados, 0, Math.min(dados.length, 6), StandardCharsets.ISO_8859_1);
            return assinatura.startsWith("GIF87a") || assinatura.startsWith("GIF89a");
        }
        if (metadados.contains("image/webp")) {
            if (dados.length < 12) return false;
            return dados[0] == 'R' && dados[1] == 'I' && dados[2] == 'F' && dados[3] == 'F'
                    && dados[8] == 'W' && dados[9] == 'E' && dados[10] == 'B' && dados[11] == 'P';
        }
        return false;
    }
}
