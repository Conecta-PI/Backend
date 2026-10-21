# 🎓 API Conecta - Trabalho Acadêmico (Senac)

Esta é a base de uma API RESTful desenvolvida em Java com Spring Boot, estruturada no padrão arquitetural de 3 camadas (Controllers, Services e Repositories). O projeto foi desenhado para gerenciar as operações de um sistema de confeitaria (PG Docinhos), conectando entidades de Usuários, Clientes, Produtos, Vendas e Movimentações Financeiras.

## 🛠️ Tecnologias e Dependências Necessárias

Para garantir a melhor arquitetura e boas práticas do mercado, o ecossistema deste projeto (`pom.xml`) foi configurado com as seguintes dependências:

*   **Spring Web:** Essencial para expor os endpoints REST (Controllers) e rodar o servidor web Apache Tomcat embutido na porta 8080.
*   **Spring Data JPA:** Responsável pela camada de persistência. Permite a comunicação com o banco de dados e o mapeamento relacional das Entidades sem a necessidade de escrever SQL puro.
*   **H2 Database:** Banco de dados relacional em memória. Ideal para testes e ambiente acadêmico, pois é rápido, não exige instalações externas e zera a cada reinicialização, mantendo o ambiente limpo.
*   **Lombok:** Ferramenta vital para reduzir a verbosidade do código Java. Ela gera automaticamente (em tempo de compilação) os Getters, Setters e Construtores essenciais para o funcionamento das entidades.
*   **Validation (Spring Boot Starter Validation):** Utilizado para aplicar validações de integridade nos dados de entrada (como `@NotNull`) antes que eles cheguem ao banco de dados.
*   **Spring Boot DevTools:** Utilitário que agiliza o desenvolvimento, permitindo reinicializações rápidas (Live Reload) sempre que houver alterações no código.

---

## 🚀 Como Executar o Projeto Corretamente

⚠️ **ATENÇÃO:** Para que o projeto funcione perfeitamente, o servidor web inicie corretamente e as anotações do Lombok sejam compiladas sem problemas visuais ou quebras de classe, **NÃO utilize os botões nativos de "Play/Run" da sua IDE sem configuração prévia.** 

O projeto deve ser executado obrigatoriamente através do utilitário do **Maven pelo terminal do VS Code**. Caso contrário, o projeto poderá apresentar erros de compilação ou comportamentos inesperados.

**Passo a passo:**

1. Abra o projeto no Visual Studio Code.
2. Abra o terminal integrado (atalho: `` Ctrl + ` `` ou navegue no topo em `Terminal > Novo Terminal`).
3. Certifique-se de que o caminho do terminal está na raiz do projeto (onde o arquivo `pom.xml` está localizado).
4. Rode o comando abaixo para iniciar o servidor:
   ```bash
   ./mvnw spring-boot:run