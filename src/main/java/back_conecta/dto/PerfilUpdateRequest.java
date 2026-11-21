package back_conecta.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class PerfilUpdateRequest {
        @Size(max = 100)
        private String nome;
        @Size(max = 150)
        private String negocio;
        @Size(max = 20)
        private String telefone;
        @Email @Size(max = 100)
        private String email;
        @Size(max = 18)
        private String cnpj;
        @Size(max = 150)
        private String chavePix;
        private String foto;
        private Boolean premium;
        private boolean fotoEnviada;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getNegocio() { return negocio; }
        public void setNegocio(String negocio) { this.negocio = negocio; }
        public String getTelefone() { return telefone; }
        public void setTelefone(String telefone) { this.telefone = telefone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }
        public String getChavePix() { return chavePix; }
        public void setChavePix(String chavePix) { this.chavePix = chavePix; }
        public String getFoto() { return foto; }
        @JsonSetter("foto")
        public void setFoto(String foto) {
                this.foto = foto;
                this.fotoEnviada = true;
        }
        public Boolean getPremium() { return premium; }
        public void setPremium(Boolean premium) { this.premium = premium; }
        public boolean isFotoEnviada() { return fotoEnviada; }
}
