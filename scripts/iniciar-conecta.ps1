$ErrorActionPreference = "Stop"

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
$backendScript = Join-Path $projectRoot "Backend\scripts\iniciar-back.ps1"
$frontScript = Join-Path $projectRoot "Front\scripts\iniciar-front.ps1"
$powerShellName = if ($PSVersionTable.PSEdition -eq "Core") { "pwsh.exe" } else { "powershell.exe" }
$powerShellCommand = Get-Command $powerShellName -ErrorAction SilentlyContinue

foreach ($script in @($backendScript, $frontScript)) {
    if (-not (Test-Path $script)) {
        throw "Script de inicializacao '$script' nao encontrado. Confira se as pastas Backend e Front estao lado a lado."
    }
}
if ($null -eq $powerShellCommand) {
    throw "Executavel '$powerShellName' nao encontrado."
}
$powerShell = $powerShellCommand.Source

Write-Host ""
Write-Host "=== Inicializacao do Conecta (back + front) ===" -ForegroundColor Cyan
Write-Host "  1 - Primeira vez no projeto? (instalar programas, extensoes e configurar o ambiente)"
Write-Host "  2 - Ja configurei: escolher o banco e iniciar back + front"
Write-Host ""

$mode = Read-Host "Escolha 1 ou 2 (Q para sair)"
if ($mode -notin @("1", "2")) {
    Write-Host "Inicializacao cancelada."
    exit 0
}

$databaseChoice = Read-Host "Qual banco deseja usar? 1 - H2 em memoria | 2 - MySQL local (Q para sair)"
if ($databaseChoice -notin @("1", "2")) {
    Write-Host "Inicializacao cancelada."
    exit 0
}
$database = if ($databaseChoice -eq "1") { "h2" } else { "mysql" }

if ($mode -eq "1") {
    Write-Host ""
    Write-Host "Verificando pre-requisitos para a primeira configuracao..."
    $missingPackages = @()
    $missingDownloads = @()

    $javaCommand = Get-Command java -ErrorAction SilentlyContinue
    $javacCommand = Get-Command javac.exe -ErrorAction SilentlyContinue
    if ($null -eq $javaCommand -or $null -eq $javacCommand) {
        $missingPackages += [pscustomobject]@{
            Name = "Java JDK 21 ou superior"
            Id = "EclipseAdoptium.Temurin.21.JDK"
            Url = "https://adoptium.net/temurin/releases/?version=21"
            Help = "Instale o JDK 21 LTS (inclui o compilador javac). Depois feche e reabra o PowerShell."
        }
    }
    else {
        $javaOutput = (& $env:ComSpec /c "java -version 2>&1" | Out-String)
        $javaVersionMatch = [regex]::Match($javaOutput, 'version\s+"(?<version>\d+)')
        if (-not $javaVersionMatch.Success -or [int]$javaVersionMatch.Groups["version"].Value -lt 21) {
            $missingPackages += [pscustomobject]@{
                Name = "Java JDK 21 ou superior"
                Id = "EclipseAdoptium.Temurin.21.JDK"
                Url = "https://adoptium.net/temurin/releases/?version=21"
                Help = "A versao atual do Java nao atende aos requisitos do projeto. Instale o JDK 21 LTS e reabra o PowerShell."
            }
        }
        else {
            Write-Host "Java JDK $($javaVersionMatch.Groups['version'].Value): encontrado." -ForegroundColor Green
        }
    }

    if (
        -not (Get-Command npm.cmd -ErrorAction SilentlyContinue) -or
        -not (Get-Command node.exe -ErrorAction SilentlyContinue)
    ) {
        $missingPackages += [pscustomobject]@{
            Name = "Node.js LTS (inclui npm)"
            Id = "OpenJS.NodeJS.LTS"
            Url = "https://nodejs.org/en/download"
            Help = "Baixe e instale o Node.js LTS, que inclui o npm. Depois, reabra o PowerShell."
        }
    }
    else {
        Write-Host "Node.js e npm: encontrados." -ForegroundColor Green
    }

    if (-not (Get-Command git.exe -ErrorAction SilentlyContinue)) {
        $missingPackages += [pscustomobject]@{
            Name = "Git para Windows"
            Id = "Git.Git"
            Url = "https://git-scm.com/download/win"
            Help = "Instale o Git para clonar e atualizar o repositorio."
        }
    }
    else {
        Write-Host "Git: encontrado." -ForegroundColor Green
    }

    $codeCommand = Get-Command code -ErrorAction SilentlyContinue
    if ($null -eq $codeCommand) {
        $codePaths = @(
            (Join-Path $env:LOCALAPPDATA "Programs\Microsoft VS Code\bin\code.cmd"),
            (Join-Path $env:ProgramFiles "Microsoft VS Code\bin\code.cmd")
        )
        $codePath = $codePaths | Where-Object { Test-Path $_ } | Select-Object -First 1
        if (-not $codePath) {
            $missingPackages += [pscustomobject]@{
                Name = "Visual Studio Code"
                Id = "Microsoft.VisualStudioCode"
                Url = "https://code.visualstudio.com/Download"
                Help = "Instale o VS Code para abrir o projeto e instalar as extensoes recomendadas."
            }
        }
    }
    else {
        $codePath = $codeCommand.Source
    }

    if ($database -eq "mysql") {
        $mysqlServices = @(Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue)
        if ($mysqlServices.Count -eq 0) {
            $missingPackages += [pscustomobject]@{
                Name = "MySQL Server para Windows"
                Id = "Oracle.MySQL"
                Url = "https://dev.mysql.com/downloads/installer/"
                Help = "Instale o MySQL Server 8.x, mantenha a porta 3306 e configure uma senha local para root. O Workbench e opcional."
            }
            $instalarWorkbench = Read-Host "Deseja instalar tambem o MySQL Workbench para consultar as tabelas? (S/N)"
            if ($instalarWorkbench -match "^(s|sim)$") {
                $missingPackages += [pscustomobject]@{
                    Name = "MySQL Workbench (opcional)"
                    Id = "Oracle.MySQLWorkbench"
                    Url = "https://dev.mysql.com/downloads/workbench/"
                    Help = "Interface grafica opcional para consultar e administrar o banco MySQL."
                }
            }
        }
        elseif ($mysqlServices.Count -gt 1) {
            throw "Foram encontrados varios servicos MySQL. Escolha qual servico usar antes de continuar."
        }
        else {
            Write-Host "Servico MySQL '$($mysqlServices[0].Name)': encontrado." -ForegroundColor Green
        }
    }
    else {
        Write-Host "O H2 nao precisa ser baixado nem instalado separadamente; o Maven o obtem como dependencia do projeto." -ForegroundColor Green
    }

    $recommendedExtensions = @(
        "vscjava.vscode-java-pack",
        "vmware.vscode-spring-boot",
        "expo.vscode-expo-tools"
    )
    $extensionsToInstall = @()
    if ($codePath) {
        $installedExtensions = @(& $codePath --list-extensions 2>$null)
        $extensionsToInstall = @($recommendedExtensions | Where-Object { $_ -notin $installedExtensions })
        if ($extensionsToInstall.Count -eq 0) {
            Write-Host "Extensoes recomendadas do VS Code: encontradas." -ForegroundColor Green
        }
    }
    else {
        Write-Host "VS Code ainda nao esta instalado. As extensoes recomendadas serao verificadas na proxima execucao de 'Primeira vez'."
    }

    if ($missingPackages.Count -gt 0 -or $extensionsToInstall.Count -gt 0) {
        Write-Host ""
        if ($missingPackages.Count -gt 0) {
            Write-Host "Itens que ainda precisam ser instalados:" -ForegroundColor Yellow
            foreach ($package in $missingPackages) {
                Write-Host "- $($package.Name): $($package.Help)"
            }
        }
        if ($extensionsToInstall.Count -gt 0) {
            Write-Host "Extensoes do VS Code que podem ser instaladas: $($extensionsToInstall -join ', ')"
        }

        $wingetCommand = Get-Command winget.exe -ErrorAction SilentlyContinue
        if ($missingPackages.Count -gt 0 -and $null -ne $wingetCommand) {
            Write-Host ""
            Write-Host "O winget sera usado somente para os itens acima; a instalacao do MySQL pode abrir o instalador oficial."
            $installPackages = Read-Host "Deseja baixar e instalar esses programas agora? (S/N)"
            if ($installPackages -match "^(s|sim)$") {
                foreach ($package in $missingPackages) {
                    Write-Host ""
                    Write-Host "Instalando $($package.Name) pelo winget..."
                    & $wingetCommand.Source install --source winget --id $package.Id --exact --accept-package-agreements --accept-source-agreements --interactive
                    if ($LASTEXITCODE -ne 0) {
                        throw "A instalacao de $($package.Name) falhou (codigo $LASTEXITCODE). Download oficial: $($package.Url)"
                    }
                }
            }
            else {
                foreach ($package in $missingPackages) {
                    $missingDownloads += $package
                }
            }
        }
        elseif ($missingPackages.Count -gt 0) {
            $missingDownloads += $missingPackages
        }

        if ($missingDownloads.Count -gt 0) {
            Write-Host ""
            Write-Host "Abra os downloads oficiais e instale os itens manualmente:"
            foreach ($package in $missingDownloads) {
                Write-Host "- $($package.Name): $($package.Url)"
                try {
                    Start-Process $package.Url
                }
                catch {
                    Write-Host "  Nao foi possivel abrir o navegador automaticamente; copie o endereco acima."
                }
            }
        }

        if ($extensionsToInstall.Count -gt 0) {
            Write-Host ""
            $installExtensions = Read-Host "Deseja instalar as extensoes recomendadas do VS Code agora? (S/N)"
            if ($installExtensions -match "^(s|sim)$") {
                foreach ($extension in $extensionsToInstall) {
                    & $codePath --install-extension $extension
                    if ($LASTEXITCODE -ne 0) {
                        throw "Nao foi possivel instalar a extensao $extension pelo comando code."
                    }
                }
            }
            else {
                Write-Host "Abra as pastas Backend e Front no VS Code para ver as recomendacoes de extensoes."
            }
        }

        if ($missingPackages.Count -gt 0) {
            Write-Host ""
            Write-Host "A instalacao dos programas foi iniciada. Feche e reabra o terminal e o VS Code, execute novamente e escolha 'Primeira vez' para instalar as extensoes. Depois use 'Ja configurei' para trabalhar."
            exit 0
        }
    }

    $frontModules = Join-Path $projectRoot "Front\node_modules"
    $frontLock = Join-Path $projectRoot "Front\package-lock.json"
    if (-not (Test-Path $frontModules)) {
        if (-not (Test-Path $frontLock)) {
            throw "package-lock.json do front nao encontrado; nao posso instalar dependencias de forma reproduzivel."
        }
        Write-Host ""
        Write-Host "Instalando as dependencias do front com npm ci..."
        Push-Location (Join-Path $projectRoot "Front")
        try {
            npm.cmd ci
            if ($LASTEXITCODE -ne 0) {
                throw "A instalacao das dependencias do front falhou (codigo $LASTEXITCODE)."
            }
        }
        finally {
            Pop-Location
        }
    }

    Write-Host "Pre-requisitos encontrados. Continuando a inicializacao." -ForegroundColor Green
}

try {
    $existingConnection = New-Object System.Net.Sockets.TcpClient
    $existingConnection.Connect("127.0.0.1", 8080)
    $existingConnection.Close()
    throw "A porta 8080 ja esta em uso. Feche o back que ja esta rodando pressionando Ctrl+C na janela dele. Nao feche esta janela do iniciador. Depois execute novamente."
}
catch [System.Net.Sockets.SocketException] {
    # A porta esta livre para iniciar o back.
}
finally {
    if ($null -ne $existingConnection) {
        $existingConnection.Close()
    }
}

$backendArguments = '-NoExit -ExecutionPolicy Bypass -File "{0}" -Database {1}' -f $backendScript, $database
$backendProcess = Start-Process -FilePath $powerShell -ArgumentList $backendArguments -PassThru
Write-Host "Janela do back aberta (PID $($backendProcess.Id))."

Write-Host "Aguardando a API responder na porta 8080..."
$apiReady = $false
for ($attempt = 1; $attempt -le 90; $attempt++) {
    if ($backendProcess.HasExited) {
        throw "A janela do back encerrou antes de a API ficar pronta. Confira o erro nessa janela e execute novamente."
    }

    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.Connect("127.0.0.1", 8080)
        $apiReady = $true
        $connection.Close()
        break
    }
    catch {
        # A porta pode ainda nao estar aberta enquanto o Spring Boot inicia.
    }
    finally {
        if ($null -ne $connection) {
            $connection.Close()
        }
    }
    Start-Sleep -Seconds 1
}

if (-not $apiReady) {
    throw "A API nao ficou pronta na porta 8080 em 90 segundos. Confira os logs na janela do back; o front nao sera iniciado."
}

$frontArguments = '-NoExit -ExecutionPolicy Bypass -File "{0}"' -f $frontScript
$frontProcess = Start-Process -FilePath $powerShell -ArgumentList $frontArguments -PassThru
Write-Host "Janela do front aberta (PID $($frontProcess.Id))."
Write-Host ""
Write-Host "Se uma porta ja estiver em uso, feche a instancia antiga antes de tentar novamente."
Write-Host "Para encerrar, pressione Ctrl+C em cada janela."
