param(
    [ValidateSet("h2", "mysql")]
    [string]$Database
)

$ErrorActionPreference = "Stop"

$backendPath = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$mavenWrapper = Join-Path $backendPath "mvnw.cmd"

if (-not (Test-Path $mavenWrapper)) {
    throw "Maven Wrapper nao encontrado em '$mavenWrapper'."
}

Write-Host ""
if ([string]::IsNullOrWhiteSpace($Database)) {
    Write-Host "=== Inicializacao guiada do back-end Conecta ===" -ForegroundColor Cyan
    Write-Host "Este assistente nao salva senhas em arquivos."
    Write-Host ""
    Write-Host "Escolha o banco desta execucao:"
    Write-Host "  1 - H2 em memoria (mais simples; os dados sao recriados ao reiniciar)"
    Write-Host "  2 - MySQL local (os dados ficam no servidor MySQL configurado)"

    $opcao = Read-Host "Digite 1 ou 2 (ou Q para sair)"
    if ($opcao -notin @("1", "2")) {
        Write-Host "Inicializacao cancelada."
        exit 0
    }
    $Database = if ($opcao -eq "1") { "h2" } else { "mysql" }
}

$environmentNames = @(
    "SPRING_PROFILES_ACTIVE",
    "MYSQL_URL",
    "MYSQL_USER",
    "MYSQL_PASSWORD"
)
$previousEnvironment = @{}
foreach ($name in $environmentNames) {
    $previousEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, "Process")
}

$securePassword = $null
$plainPassword = $null

try {
    if ($Database -eq "h2") {
        $env:SPRING_PROFILES_ACTIVE = "h2"
        Remove-Item Env:MYSQL_URL -ErrorAction SilentlyContinue
        Remove-Item Env:MYSQL_USER -ErrorAction SilentlyContinue
        Remove-Item Env:MYSQL_PASSWORD -ErrorAction SilentlyContinue

        Write-Host ""
        Write-Host "Banco escolhido: H2 em memoria." -ForegroundColor Green
        Write-Host "Quando o back iniciar, o console estara em http://localhost:8080/h2-console"
        Write-Host "JDBC URL: jdbc:h2:mem:conecta_db | Usuario: sa | Senha: vazia"
        Write-Host "Os dados do H2 sao locais a esta execucao e somem quando o back reinicia."
    }
    else {
        Write-Host ""
        $mysqlServices = @(Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue)
        if ($mysqlServices.Count -eq 1) {
            $mysqlService = $mysqlServices[0]
            if ($mysqlService.Status -ne "Running") {
                Write-Host "O servico MySQL '$($mysqlService.Name)' esta parado. Tentando inicia-lo..."
                try {
                    Start-Service -Name $mysqlService.Name -ErrorAction Stop
                    $mysqlService.WaitForStatus("Running", [TimeSpan]::FromSeconds(30))
                    Write-Host "Servico MySQL iniciado." -ForegroundColor Green
                }
                catch {
                    throw "Nao foi possivel iniciar o servico MySQL '$($mysqlService.Name)'. Abra o PowerShell como Administrador, inicie o servico e execute novamente. Detalhe: $($_.Exception.Message)"
                }
            }
            else {
                Write-Host "O servico MySQL '$($mysqlService.Name)' ja esta em execucao." -ForegroundColor Green
            }
        }
        elseif ($mysqlServices.Count -gt 1) {
            $serviceNames = ($mysqlServices | ForEach-Object { $_.Name }) -join ", "
            throw "Foram encontrados varios servicos MySQL ($serviceNames); nao vou escolher um automaticamente. Informe qual servico usar ou deixe apenas o servico correto instalado."
        }
        else {
            Write-Host "Nao encontrei um servico Windows com nome MySQL*."
            Write-Host "Confirme que o MySQL Server esta instalado e que o servico esta configurado."
            $continuar = Read-Host "Deseja continuar mesmo assim? (S/N)"
            if ($continuar -notmatch "^(s|sim)$") {
                Write-Host "Inicializacao cancelada."
                exit 0
            }
        }

        Write-Host ""
        Write-Host "Voce vai informar os dados da conexao MySQL. Ao final, eles serao exibidos para confirmacao."
        Write-Host "Essa confirmacao nao permite editar os dados; digite Q para cancelar e iniciar novamente."

        $hostMySql = Read-Host "Host MySQL [localhost]"
        if ([string]::IsNullOrWhiteSpace($hostMySql)) { $hostMySql = "localhost" }

        $portaMySql = Read-Host "Porta MySQL [3306]"
        if ([string]::IsNullOrWhiteSpace($portaMySql)) { $portaMySql = "3306" }
        $portaNumerica = 0
        if (-not [int]::TryParse($portaMySql, [ref]$portaNumerica) -or $portaNumerica -lt 1 -or $portaNumerica -gt 65535) {
            throw "Porta MySQL invalida. Informe um numero entre 1 e 65535."
        }

        $nomeBanco = Read-Host "Nome do banco [conecta]"
        if ([string]::IsNullOrWhiteSpace($nomeBanco)) { $nomeBanco = "conecta" }
        if ($nomeBanco -notmatch "^[A-Za-z0-9_$-]+$") {
            throw "Nome do banco invalido. Use apenas letras, numeros, sublinhado ou hifen."
        }

        $usuarioMySql = Read-Host "Usuario MySQL [root]"
        if ([string]::IsNullOrWhiteSpace($usuarioMySql)) { $usuarioMySql = "root" }

        $securePassword = Read-Host "Senha MySQL (nao aparecera na tela)" -AsSecureString
        if ($securePassword.Length -eq 0) {
            $securePassword.Dispose()
            $securePassword = $null
            throw "A senha MySQL nao pode ficar vazia neste fluxo."
        }

        Write-Host ""
        Write-Host "Confira os dados informados para a conexao MySQL" -ForegroundColor Yellow
        Write-Host "Servidor -> $hostMySql"
        Write-Host "Porta -> $portaNumerica"
        Write-Host "Banco -> $nomeBanco (sera criado se ainda nao existir)"
        Write-Host "Usuario -> $usuarioMySql"
        Write-Host "Senha -> informada e oculta"
        Write-Host "Esses dados serao usados para conectar ao banco e salvar os dados."
        $confirmacao = Read-Host "Digite S para confirmar ou Q para cancelar"

        if ($confirmacao -notmatch "^(s|sim)$") {
            $securePassword.Dispose()
            $securePassword = $null
            Write-Host "Inicializacao cancelada. Execute novamente para informar os dados corretos."
            exit 0
        }

        $plainPassword = (New-Object System.Net.NetworkCredential("", $securePassword)).Password

        $env:SPRING_PROFILES_ACTIVE = "mysql"
        $env:MYSQL_URL = "jdbc:mysql://${hostMySql}:${portaNumerica}/${nomeBanco}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        $env:MYSQL_USER = $usuarioMySql
        $env:MYSQL_PASSWORD = $plainPassword

        Write-Host ""
        Write-Host "Banco escolhido: MySQL em ${hostMySql}:${portaNumerica}/${nomeBanco}." -ForegroundColor Green
        Write-Host "Depois que a API iniciar, consulte o banco no Workbench."
        Write-Host "Nunca compartilhe a senha do MySQL com quem so precisa usar o aplicativo."
    }

    Write-Host ""
    Write-Host "Proximos passos:"
    Write-Host "  1. Aguarde a mensagem 'Started ApiJavaApplication'."
    Write-Host "  2. Deixe esta janela aberta para manter o back em execucao."
    Write-Host "  3. Em outra janela, inicie o front com: Set-Location ..\Front; .\scripts\iniciar-front.ps1"
    Write-Host "  4. Em celular fisico, use a mesma rede Wi-Fi do computador que executa o Expo e o back."
    Write-Host "  5. Entre com o e-mail e a senha da conta do responsavel ja cadastrada."
    Write-Host "  6. Para parar o back, pressione Ctrl+C nesta janela."
    Write-Host ""

    Push-Location $backendPath
    try {
        & $mavenWrapper spring-boot:run
        if ($LASTEXITCODE -ne 0) {
            throw "O back encerrou com codigo $LASTEXITCODE. Confira as mensagens acima."
        }
    }
    finally {
        Pop-Location
    }
}
finally {
    foreach ($name in $environmentNames) {
        $previousValue = $previousEnvironment[$name]
        if ($null -eq $previousValue) {
            Remove-Item "Env:$name" -ErrorAction SilentlyContinue
        }
        else {
            [Environment]::SetEnvironmentVariable($name, $previousValue, "Process")
        }
    }

    if ($null -ne $plainPassword) {
        $plainPassword = $null
    }
    if ($null -ne $securePassword) {
        $securePassword.Dispose()
    }
}
