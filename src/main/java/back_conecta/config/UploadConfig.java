package back_conecta.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadConfig implements WebMvcConfigurer {
    private final Path diretorioPerfil;

    public UploadConfig(@Value("${app.upload.perfil-dir:./uploads/perfil}") String diretorioPerfil) {
        this.diretorioPerfil = Path.of(diretorioPerfil).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/perfil/**")
                .addResourceLocations(diretorioPerfil.toUri().toString());
    }
}
