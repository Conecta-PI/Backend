param(
    [string]$Servidor = "localhost",
    [string]$Port = "3306",
    [string]$Usuario = "root",
    [string]$Banco = "conecta",
    [string]$Senha = ""
)

$ErrorActionPreference = "Stop"

$scriptPath = Join-Path $PSScriptRoot "seed-mysql.sql"
if (-not (Test-Path $scriptPath)) {
    throw "Arquivo nao encontrado: $scriptPath"
}

if (-not (Get-Command mysql -ErrorAction SilentlyContinue)) {
    throw "Cliente mysql nao encontrado no PATH. Instale o MySQL Client antes de executar esse script."
}

$mysqlArgs = @("-h", $Servidor, "-P", $Port, "-u", $Usuario)
if ($Senha) {
    $mysqlArgs += "-p$Senha"
}
$mysqlArgs += $Banco

Write-Host "Executando seed do MySQL em ${Servidor}:${Port}/$Banco..." -ForegroundColor Cyan
Get-Content $scriptPath | & mysql @mysqlArgs

if ($LASTEXITCODE -ne 0) {
    throw "O script encerrou com erro. Verifique a senha, o banco e a estrutura do schema."
}

Write-Host "Seed concluido com sucesso." -ForegroundColor Green
Write-Host "Valide os resultados com: SELECT COUNT(*) FROM CLIENTES; SELECT COUNT(*) FROM PRODUTOS;" -ForegroundColor Yellow
