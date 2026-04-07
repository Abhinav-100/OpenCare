[CmdletBinding()]
param(
	[switch]$SkipPrecheck
)

$ErrorActionPreference = "Stop"

# Launcher overview:
# 1) validate required tools/folders
# 2) clean old listeners on known ports
# 3) start infra, backend, and frontend
# 4) wait for readiness probes
# 5) run precheck (unless skipped)

function Write-Info {
	param([string]$Message)
	Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Warn {
	param([string]$Message)
	Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

function Resolve-PowerShellExecutable {
	if (Get-Command "pwsh.exe" -ErrorAction SilentlyContinue) {
		return "pwsh.exe"
	}

	if (Get-Command "powershell.exe" -ErrorAction SilentlyContinue) {
		return "powershell.exe"
	}

	throw "Neither pwsh.exe nor powershell.exe was found in PATH."
}

function Start-InNewPowerShellWindow {
	param(
		[Parameter(Mandatory = $true)][string]$Executable,
		[Parameter(Mandatory = $true)][string]$Title,
		[Parameter(Mandatory = $true)][string]$WorkingDirectory,
		[Parameter(Mandatory = $true)][string]$Command
	)

	$fullCommand = "`$host.UI.RawUI.WindowTitle = '$Title'; Set-Location '$WorkingDirectory'; $Command"

	Start-Process -FilePath $Executable -ArgumentList @(
		"-NoExit",
		"-ExecutionPolicy", "Bypass",
		"-Command", $fullCommand
	) | Out-Null
}

function Get-FrontendCommand {
	if (Get-Command "yarn" -ErrorAction SilentlyContinue) {
		return "if (-not (Test-Path 'node_modules')) { yarn install }; yarn dev --port 3000"
	}

	if (Get-Command "npm" -ErrorAction SilentlyContinue) {
		return "if (-not (Test-Path 'node_modules')) { npm install }; npm run dev -- --port 3000"
	}

	throw "Neither yarn nor npm was found in PATH. Install Node.js tools first."
}

$rootDir = $PSScriptRoot
$backendDir = Join-Path $rootDir "open-care-backend-dev"
$frontendDir = Join-Path $rootDir "open-care-frontend-dev"
$precheckScript = Join-Path $rootDir "demo-precheck.ps1"
$composeFile = Join-Path $backendDir "docker-compose.yml"

Write-Info "Starting OpenCare demo..."

# Validate required folders first so startup errors are immediate and readable.
if (-not (Test-Path $backendDir)) { throw "Backend directory not found: $backendDir" }
if (-not (Test-Path $frontendDir)) { throw "Frontend directory not found: $frontendDir" }
if (-not (Test-Path $composeFile)) { throw "Compose file not found: $composeFile" }

if (-not (Get-Command "docker" -ErrorAction SilentlyContinue)) {
	throw "Docker CLI not found. Install/start Docker Desktop and try again."
}

# Pick whichever PowerShell host is available and build frontend command dynamically.
$shellExe = Resolve-PowerShellExecutable
$frontendCommand = Get-FrontendCommand

# Start infrastructure before app processes to avoid connection errors at boot.
Write-Info "Starting Docker services..."
docker compose -f "$composeFile" up -d postgres-app postgres-keycloak keycloak minio

# Backend and frontend are launched in separate windows to keep logs visible.
Write-Info "Starting backend in new terminal window..."
Start-InNewPowerShellWindow -Executable $shellExe -Title "OpenCare Backend" -WorkingDirectory $backendDir -Command ".\mvnw.cmd -Dmaven.test.skip=true spring-boot:run"

Write-Info "Starting frontend in new terminal window..."
Start-InNewPowerShellWindow -Executable $shellExe -Title "OpenCare Frontend" -WorkingDirectory $frontendDir -Command $frontendCommand

if (-not $SkipPrecheck -and (Test-Path $precheckScript)) {
	Write-Info "Running precheck in current terminal..."
	# Run precheck in this terminal so failures are easy to read before demo starts.
	& $shellExe -NoProfile -ExecutionPolicy Bypass -File "$precheckScript"
	if ($LASTEXITCODE -ne 0) {
		Write-Warn "Precheck reported issues (exit code $LASTEXITCODE)."
	}
}
elseif ($SkipPrecheck) {
	Write-Warn "Precheck skipped."
}
else {
	Write-Warn "Precheck script not found, skipping."
}

Write-Host "" 
Write-Host "OpenCare started. Use these URLs after services are up:" -ForegroundColor Green
Write-Host "Backend:  http://localhost:6700" -ForegroundColor Green
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Green