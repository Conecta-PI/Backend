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
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

## Perfis de banco de dados para desenvolvimento

O projeto mantém o H2 temporário e o driver MySQL disponíveis, mas cada execução da API deve usar **um único banco**. O perfil padrão é `h2`; os dados ficam em memória e são recriados ao reiniciar. Os seis produtos e quatro clientes que existiam como exemplos no front são carregados no H2 e no MySQL somente quando ainda não existem. Os telefones e e-mails `example.test` são fictícios. O custo dos produtos não foi inventado: permanece nulo até ser informado.

### Primeira configuração e inicializações seguintes (Windows/PowerShell)

Se ainda não baixou o projeto, instale o [Git para Windows](https://git-scm.com/download/win) (ou escolha **Primeira vez** no assistente para instalá-lo pelo WinGet), clone o repositório pelo botão **Code > Clone** da página do projeto e abra a pasta `conecta` no VS Code. No terminal PowerShell, use o mesmo comando para iniciar back e front:

```powershell
Set-Location C:\projetos\conecta\Backend
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\iniciar-conecta.ps1
```

O assistente primeiro pergunta se é a primeira configuração neste computador ou se o projeto já foi configurado:

- **Primeira vez:** escolha H2 ou MySQL. O assistente verifica Java JDK 21+, Node.js LTS, Git e VS Code. Se algo estiver faltando, pode baixar e instalar os programas pelo WinGet depois da sua confirmacao; sem WinGet, abre os downloads oficiais para instalacao manual. Ao escolher MySQL, tambem oferece o Workbench opcional. H2 ja vem como dependencia do projeto e nao precisa de instalacao separada. Se novos programas forem instalados, feche e reabra o terminal e o VS Code e execute o assistente outra vez em **Primeira vez** para configurar extensoes e dependencias do front; depois use **Ja configurei** nas sessoes de trabalho.
- **Extensoes do VS Code:** na primeira configuracao, o assistente verifica e oferece instalar Extension Pack for Java, Spring Boot Extension Pack e Expo Tools. O suporte as anotacoes Lombok e fornecido pela extensao oficial de Java; nao e necessaria uma extensao Lombok separada. Os arquivos `Backend/.vscode/extensions.json` e `Front/.vscode/extensions.json` tambem mostram as recomendacoes ao abrir cada pasta no VS Code.
- **Ja configurei:** escolha o banco para esta execucao. Com MySQL, o assistente tenta iniciar o servico local e solicita as credenciais; com H2, usa o banco temporario em memoria. Se as dependencias do front nao estiverem instaladas, o assistente as restaura com `npm ci`. Antes de iniciar o back, verifica se a porta `8080` esta livre. Se ja houver outro back em execucao, pressione `Ctrl+C` na janela dele e execute o iniciador novamente. O assistente nao encerra processos automaticamente. Quando a API estiver pronta, abre o front.

Antes de solicitar os dados do MySQL, o assistente avisa que haverá uma conferência. Depois de informar host, porta, banco, usuário e senha, ele exibe os valores informados (a senha fica oculta) e pede somente confirmação: digite `S` para prosseguir ou `Q` para cancelar. Essa etapa não aceita correções; para alterar algum dado, cancele e execute novamente. Mantenha abertas as janelas do back e do front durante o uso; escaneie o QR code do Expo Go no celular conectado à mesma rede Wi-Fi. Para parar, pressione `Ctrl+C` em cada janela. Os dados do H2 são apagados ao reiniciar e não aparecem no MySQL.

Para iniciar somente um componente, use `.\scripts\iniciar-back.ps1` na pasta `Backend` ou `.\scripts\iniciar-front.ps1` na pasta `Front`. No primeiro uso isolado, confira e instale os pré-requisitos indicados acima.

### Usar H2 (padrão)

```powershell
.\mvnw.cmd spring-boot:run
```

Console H2: `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:conecta_db`, usuário `sa`, senha vazia.

### Usar MySQL

#### Preparar o MySQL Server no Windows

1. Instale **MySQL Server 8.x** usando o instalador oficial MySQL Installer. O **MySQL Workbench é opcional**: ele é uma interface gráfica de administração, não substitui o servidor.
2. Na instalação, mantenha o serviço Windows `MySQL80` e configure uma senha local para `root`. Não compartilhe essa senha nem a salve no projeto.
3. Abra `services.msc` e confirme que o serviço MySQL está como **Em execução**. Se estiver parado, clique em **Iniciar**. Alternativamente, use PowerShell como administrador: `Start-Service MySQL80`.
4. Se usar o Workbench, crie uma conexão local com host `localhost`, porta `3306` e usuário `root`; valide a conexão. O banco `conecta` é criado pela aplicação, portanto não precisa criá-lo manualmente.
5. No terminal do VS Code, entre na pasta `Backend` e rode os comandos abaixo. A senha é solicitada sem ser gravada no histórico do terminal e fica disponível apenas no ambiente desse processo.

```powershell
Set-Location C:\projetos\conecta\Backend
$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:MYSQL_URL = "jdbc:mysql://localhost:3306/conecta?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:MYSQL_USER = "root"
$senhaMySQL = Read-Host "Senha local do MySQL" -AsSecureString
$env:MYSQL_PASSWORD = (New-Object System.Net.NetworkCredential("", $senhaMySQL)).Password
Remove-Variable senhaMySQL
.\mvnw.cmd spring-boot:run
```

Ao aparecer `Started ApiJavaApplication`, a API está rodando. O primeiro responsável cria a única conta pela tela **Criar conta**; depois, o acesso é feito com o e-mail e a senha definidos no cadastro. Os registros da tabela `CLIENTES` são contatos, não contas de login. As senhas das contas são armazenadas com hash BCrypt; não há senha padrão.

Os endpoints `POST /api/auth/register` e `POST /api/auth/login` são públicos para o fluxo inicial. As demais rotas `/api/**` exigem o token Bearer emitido no login. O app armazena o token no armazenamento seguro do dispositivo. Se o segredo JWT não for configurado, é gerado um segredo aleatório a cada inicialização; tokens anteriores deixam de valer após reiniciar o back. Para manter tokens válidos entre reinicializações, configure `JWT_SECRET` com um valor Base64 de pelo menos 32 bytes na sessão do back. Não exponha o endpoint de cadastro aberto a redes não confiáveis.

Teste os endpoints protegidos pelo aplicativo depois de entrar:

```powershell
Invoke-RestMethod "http://localhost:8080/api/dashboard?periodo=hoje" -Headers @{ Authorization = "Bearer <TOKEN>" }
```

Se `Get-Service *mysql*` não mostrar serviço, instale primeiro o **MySQL Server**. Se a API não iniciar, confira em `services.msc` se o serviço está ativo, confirme usuário/senha e veja a mensagem de erro do Maven.

#### Permitir acesso dos integrantes na rede local

O back-end já está configurado para escutar nas interfaces de rede (`server.address=0.0.0.0`) e aceitar origens privadas de desenvolvimento via CORS. Compartilhe com o grupo somente o IPv4 local do computador que executa a API — confira com `ipconfig` — e a porta `8080`. Não compartilhe a senha do MySQL: os celulares chamam a API, e somente o back-end acessa o MySQL.

Por segurança, não coloque credenciais nos READMEs, no Git ou em mensagens junto com o código. O endereço da API e as instruções de acesso ao app serão compartilhados separadamente com os integrantes pelo grupo de WhatsApp. Compartilhe apenas o e-mail e a senha da conta do app necessários para o teste; nunca envie `MYSQL_PASSWORD` ou `JWT_SECRET`. Como há uma única conta de responsável, todos os integrantes que a utilizarem terão o mesmo nível de acesso.

Para liberar a API no Firewall do Windows, abra PowerShell **como Administrador** e execute:

```powershell
New-NetFirewallRule -DisplayName "Conecta API (8080)" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Private
```

Use apenas numa rede Wi-Fi confiável marcada como **Privada**. No celular, `localhost` significa o próprio aparelho; configure o aplicativo para apontar para `http://<IPv4-DO-COMPUTADOR>:8080/api`. Se os integrantes não estiverem na mesma rede local, esse IP não estará acessível para eles: use uma VPN aprovada pelo grupo ou hospedagem; não exponha diretamente as portas 8080/3306 à internet.

O front usa `expo-constants` para detectar o host informado pelo Expo e montar `http://<IP-DO-COMPUTADOR>:8080/api`. Inicie o Expo com `npx expo start --lan`; o celular e o computador precisam estar na mesma rede Wi-Fi. Não use `localhost` no celular físico.

O Hibernate cria/atualiza as tabelas com `ddl-auto=update` durante o desenvolvimento. A carga dos seis produtos e quatro clientes de demonstração é idempotente e não apaga nem sobrescreve registros existentes. Também é criada uma preferência, uma venda concluída de demonstração, dois itens e a movimentação correspondente, identificados como dados de teste; o estoque não é alterado por essa carga. O dashboard de demonstração é armazenado em tabelas `DASHBOARD_DEMO_*` separadas das vendas e movimentações, e é consultado por `GET /api/dashboard?periodo=hoje|semana|mes`; esses valores não representam transações reais. Não é criado um usuário de demonstração com senha em texto puro. Antes de usar uma base com dados importantes, faça backup e planeje migrações; não use essa configuração automática em produção. As credenciais devem ficar em variáveis de ambiente, nunca no Git.

Para voltar ao H2, feche a API e remova o perfil MySQL desta sessão:

```powershell
Remove-Item Env:SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue
.\mvnw.cmd spring-boot:run
```

H2 e MySQL podem coexistir instalados no mesmo projeto, mas não são combinados como dois bancos da mesma execução. Para rodar duas instâncias simultaneamente seriam necessárias portas e configurações separadas.

## Endpoints disponíveis

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/produtos` | Lista produtos ativos |
| GET | `/api/produtos/{id}` | Detalha produto ativo |
| POST | `/api/produtos` | Cadastra produto |
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/vendas` | Lista vendas |
| POST | `/api/vendas` | Cria venda, valida/baixa estoque e registra movimentação financeira |

Origens web locais e endereços IPv4 privados estão liberados para desenvolvimento via CORS. Para mudar essa lista, configure `CORS_ALLOWED_ORIGIN_PATTERNS`. CORS não substitui autenticação; não exponha essa API sem autenticação à internet.

### Celular com Expo Go

O celular e o computador precisam estar na mesma rede Wi-Fi. No computador, use `ipconfig` para obter o IPv4 e configure o Expo Go para acessar `http://<IP-DO-COMPUTADOR>:8080/api`. Não use `localhost` nem `10.0.2.2` no celular físico; esses endereços são para o próprio dispositivo e para o emulador Android, respectivamente. Se a API não responder, permita conexões de entrada na porta TCP 8080 no Firewall do Windows para a rede privada.