[CmdletBinding()]
param(
	[int]$WarmupSeconds = 12,
	[int]$BackendTimeoutSeconds = 180,
	[int]$FrontendTimeoutSeconds = 180,
	[switch]$SkipPrecheck
)

$ErrorActionPreference = "Stop"

function Ensure-Command {
	param([string]$Name)

	if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
		throw "Required command '$Name' was not found in PATH."
	}
}

function Start-InNewPowerShellWindow {
	param(
		[Parameter(Mandatory = $true)][string]$Title,
		[Parameter(Mandatory = $true)][string]$Command
	)

	Start-Process -FilePath "powershell.exe" -ArgumentList @(
		"-NoExit",
		"-ExecutionPolicy", "Bypass",
		"-Command", "`$host.UI.RawUI.WindowTitle = '$Title'; $Command"
	) | Out-Null
}

function Stop-KnownProcessOnPort {
	param(
		[int]$Port,
		[string[]]$AllowedProcessNames
	)

	$listeners = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
	if (-not $listeners) {
		return
	}

	$pids = @($listeners | Select-Object -ExpandProperty OwningProcess -Unique)
	foreach ($pid in $pids) {
		try {
			$proc = Get-Process -Id $pid -ErrorAction Stop
		}
		catch {
			continue
		}

		$procName = $proc.ProcessName.ToLowerInvariant()
		if ($AllowedProcessNames -contains $procName) {
			Write-Host "[INFO] Stopping existing process '$($proc.ProcessName)' on port $Port (PID $pid)..." -ForegroundColor Yellow
			Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
		}
		else {
			Write-Host "[WARN] Port $Port is used by '$($proc.ProcessName)' (PID $pid). Stop it manually if startup fails." -ForegroundColor Yellow
		}
	}
}

function Test-BackendReady {
	param(
		[string]$BaseUrl,
		[int]$TimeoutSec = 3
	)

	try {
		$health = Invoke-RestMethod -Uri "$BaseUrl/actuator/health/readiness" -Method Get -TimeoutSec $TimeoutSec
		return ($null -ne $health -and $health.status -eq "UP")
	}
	catch {
		return $false
	}
}

function Get-ReadyFrontendPort {
	param([int]$TimeoutSec = 3)

	foreach ($port in @(3000, 3001)) {
		try {
			$response = Invoke-WebRequest -Uri "http://localhost:$port" -Method Get -TimeoutSec $TimeoutSec -UseBasicParsing
			if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
				return $port
			}
		}
		catch {
			# try next port
		}
	}

	return $null
}

function Wait-Until {
	param(
		[string]$Name,
		[int]$TimeoutSeconds,
		[scriptblock]$Probe,
		[int]$PollIntervalSeconds = 3
	)

	$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
	while ((Get-Date) -lt $deadline) {
		if (& $Probe) {
			Write-Host "[PASS] $Name is ready" -ForegroundColor Green
			return $true
		}

		Start-Sleep -Seconds $PollIntervalSeconds
	}

	Write-Host "[WARN] Timed out waiting for $Name after $TimeoutSeconds second(s)." -ForegroundColor Yellow
	return $false
}

$rootDir = $PSScriptRoot
$backendDir = Join-Path $rootDir "open-care-backend-dev"
$frontendDir = Join-Path $rootDir "open-care-frontend-dev"
$precheckScript = Join-Path $rootDir "demo-precheck.ps1"
$composeFile = Join-Path $backendDir "docker-compose.yml"

Write-Host "[INFO] Starting OpenCare demo..." -ForegroundColor Cyan

if (-not (Test-Path $backendDir)) { throw "Backend directory not found: $backendDir" }
if (-not (Test-Path $frontendDir)) { throw "Frontend directory not found: $frontendDir" }
if (-not (Test-Path $composeFile)) { throw "Compose file not found: $composeFile" }
if (-not (Test-Path $precheckScript) -and -not $SkipPrecheck) { throw "Precheck script not found: $precheckScript" }

Ensure-Command -Name "docker"
Ensure-Command -Name "powershell"
Ensure-Command -Name "yarn"

Write-Host "[INFO] Cleaning up stale demo listeners (if any)..." -ForegroundColor Cyan
Stop-KnownProcessOnPort -Port 6700 -AllowedProcessNames @("java", "javaw")
Stop-KnownProcessOnPort -Port 3000 -AllowedProcessNames @("node")
Stop-KnownProcessOnPort -Port 3001 -AllowedProcessNames @("node")

Write-Host "[INFO] Starting Docker services..." -ForegroundColor Cyan
docker compose -f "$composeFile" up -d postgres-app postgres-keycloak keycloak minio

Write-Host "[INFO] Starting backend in a new terminal..." -ForegroundColor Cyan
Start-InNewPowerShellWindow -Title "OpenCare Backend" -Command "Set-Location '$backendDir'; & '.\mvnw.cmd' @('-Dmaven.test.skip=true','spring-boot:run')"

Write-Host "[INFO] Starting frontend in a new terminal..." -ForegroundColor Cyan
Start-InNewPowerShellWindow -Title "OpenCare Frontend" -Command "Set-Location '$frontendDir'; if (-not (Test-Path 'node_modules')) { yarn install }; yarn dev --port 3000"

Write-Host "[INFO] Waiting $WarmupSeconds second(s) for services to initialize..." -ForegroundColor Cyan
Start-Sleep -Seconds $WarmupSeconds

$backendBaseUrl = "http://localhost:6700"
$readyFrontendPort = $null

$backendReady = Wait-Until -Name "backend readiness endpoint" -TimeoutSeconds $BackendTimeoutSeconds -Probe {
	Test-BackendReady -BaseUrl $backendBaseUrl -TimeoutSec 3
}

$frontendReady = Wait-Until -Name "frontend HTTP endpoint" -TimeoutSeconds $FrontendTimeoutSeconds -Probe {
	$script:readyFrontendPort = Get-ReadyFrontendPort -TimeoutSec 3
	return ($null -ne $script:readyFrontendPort)
}

if ($frontendReady) {
	Write-Host "[INFO] Frontend reachable on port $readyFrontendPort" -ForegroundColor Green
}

if (-not $backendReady -or -not $frontendReady) {
	Write-Host "[WARN] One or more services are not ready yet. Precheck may fail until startup finishes." -ForegroundColor Yellow
}

if (-not $SkipPrecheck) {
	Write-Host "[INFO] Running precheck..." -ForegroundColor Cyan
	& $precheckScript
}
else {
	Write-Host "[INFO] Precheck skipped (SkipPrecheck switch provided)." -ForegroundColor Yellow
}

Write-Host "[INFO] OpenCare startup flow completed." -ForegroundColor Green