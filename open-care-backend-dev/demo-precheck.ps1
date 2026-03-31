$rootScript = Join-Path $PSScriptRoot "..\demo-precheck.ps1"

if (-not (Test-Path $rootScript)) {
    Write-Host "[FAIL] Root precheck script not found at $rootScript" -ForegroundColor Red
    exit 1
}

& $rootScript
exit $LASTEXITCODE
