[CmdletBinding()]
param(
	[int]$WarmupSeconds = 12,
	[int]$BackendTimeoutSeconds = 180,
	[int]$FrontendTimeoutSeconds = 180,
	[switch]$SkipPrecheck,
	[switch]$FailOnPrecheck
)

$ErrorActionPreference = "Stop"

function Get-CommandPresence {
	param([string]$Name)

	return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
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

	$uniqueListeners = @($listeners | Group-Object -Property OwningProcess | ForEach-Object { $_.Group[0] })
	foreach ($listener in $uniqueListeners) {
		try {
			$proc = Get-Process -Id $listener.OwningProcess -ErrorAction Stop
		}
		catch {
			continue
		}

		$procName = $proc.ProcessName.ToLowerInvariant()
		if ($AllowedProcessNames -contains $procName) {
			Write-Host "[INFO] Stopping existing process '$($proc.ProcessName)' on port $Port (Owner $($listener.OwningProcess))..." -ForegroundColor Yellow
			Stop-Process -Id $listener.OwningProcess -Force -ErrorAction SilentlyContinue
		}
		else {
			Write-Host "[WARN] Port $Port is used by '$($proc.ProcessName)' (Owner $($listener.OwningProcess)). Stop it manually if startup fails." -ForegroundColor Yellow
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

if (-not (Get-CommandPresence -Name "docker")) { throw "Required command 'docker' was not found in PATH." }
if (-not (Get-CommandPresence -Name "powershell")) { throw "Required command 'powershell' was not found in PATH." }
if (-not (Get-CommandPresence -Name "yarn")) { throw "Required command 'yarn' was not found in PATH." }

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
	$precheckExitCode = 0
	powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$precheckScript"
	if ($null -ne $LASTEXITCODE) {
		$precheckExitCode = [int]$LASTEXITCODE
	}

	if ($precheckExitCode -ne 0) {
		Write-Host "[WARN] Precheck reported issues (exit code $precheckExitCode)." -ForegroundColor Yellow
		Write-Host "[WARN] Fix the reported items and rerun: .\start-demo.ps1" -ForegroundColor Yellow

		if ($FailOnPrecheck) {
			throw "Precheck failed with exit code $precheckExitCode (FailOnPrecheck enabled)."
		}

		# Keep launcher success semantics in non-strict mode.
		$global:LASTEXITCODE = 0
	}
	else {
		$global:LASTEXITCODE = 0
	}
}
else {
	Write-Host "[INFO] Precheck skipped (SkipPrecheck switch provided)." -ForegroundColor Yellow
	$global:LASTEXITCODE = 0
}

Write-Host "[INFO] OpenCare startup flow completed." -ForegroundColor Green