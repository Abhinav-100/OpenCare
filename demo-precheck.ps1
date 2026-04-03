param(
    [string]$BackendBaseUrl = "http://localhost:6700",
    [string]$DemoPatientEmail = "demo.patient@opencare.in",
    [PSCredential]$DemoPatientCredential = (New-Object System.Management.Automation.PSCredential(
        "demo.patient@opencare.in",
        (ConvertTo-SecureString "Demo@123A" -AsPlainText -Force)
    )),
    [int]$DemoDistrictId = 119,
    [int]$TimeoutSec = 3
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

function Write-Pass {
    param([string]$Message)
    Write-Host "[PASS] $Message" -ForegroundColor Green
}

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Fail {
    param(
        [string]$Message,
        [string]$Fix
    )
    Write-Host "[FAIL] $Message" -ForegroundColor Red
    if ($Fix) {
        Write-Host "       fix: $Fix" -ForegroundColor Yellow
    }
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Uri,
        [object]$Body = $null,
        [hashtable]$Headers = @{},
        [int]$Timeout = 3
    )

    try {
        if ($null -ne $Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 8
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers -ContentType "application/json" -Body $jsonBody -TimeoutSec $Timeout
        }
        else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers -TimeoutSec $Timeout
        }

        return @{
            ok     = $true
            status = 200
            data   = $response
            error  = ""
            raw    = ""
        }
    }
    catch {
        $status = 0
        $raw = ""

        if ($_.Exception.Response) {
            try {
                $status = [int]$_.Exception.Response.StatusCode.value__
            }
            catch {
                $status = 0
            }

            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $raw = $reader.ReadToEnd()
                    $reader.Close()
                }
            }
            catch {
                $raw = ""
            }
        }

        return @{
            ok     = $false
            status = $status
            data   = $null
            error  = $_.Exception.Message
            raw    = $raw
        }
    }
}

function Get-FirstNotEmpty {
    param([object[]]$Candidates)
    foreach ($candidate in $Candidates) {
        if ($null -ne $candidate -and "$candidate".Trim().Length -gt 0) {
            return "$candidate"
        }
    }
    return $null
}

function Get-TotalItems {
    param([object]$Response)

    if ($null -eq $Response) { return 0 }

    if ($Response.PSObject.Properties.Name -contains "totalItems") {
        return [int]$Response.totalItems
    }

    if ($Response.PSObject.Properties.Name -contains "data") {
        $data = $Response.data
        if ($null -ne $data -and $data.PSObject.Properties.Name -contains "totalItems") {
            return [int]$data.totalItems
        }
    }

    foreach ($listKey in @("doctors", "profiles", "items", "content")) {
        if ($Response.PSObject.Properties.Name -contains $listKey -and $Response.$listKey -is [System.Collections.IEnumerable]) {
            return @($Response.$listKey).Count
        }
        if ($Response.PSObject.Properties.Name -contains "data") {
            $data = $Response.data
            if ($null -ne $data -and $data.PSObject.Properties.Name -contains $listKey -and $data.$listKey -is [System.Collections.IEnumerable]) {
                return @($data.$listKey).Count
            }
        }
    }

    return 0
}

function Test-UserTypeIsPatient {
    param([object]$UserType)

    if ($null -eq $UserType) { return $false }

    if ($UserType -is [string]) {
        $value = $UserType.ToUpperInvariant()
        return ($value -eq "USER" -or $value -eq "PATIENT" -or $value -eq "ROLE_USER")
    }

    if ($UserType.PSObject.Properties.Name -contains "value" -and $null -ne $UserType.value) {
        $value = "$($UserType.value)".ToUpperInvariant()
        return ($value -eq "USER" -or $value -eq "PATIENT" -or $value -eq "ROLE_USER")
    }

    if ($UserType.PSObject.Properties.Name -contains "displayName" -and $null -ne $UserType.displayName) {
        $value = "$($UserType.displayName)".ToUpperInvariant()
        return ($value -like "*PATIENT*" -or $value -like "*USER*")
    }

    return $false
}

$summary = [ordered]@{
    Backend      = $false
    Frontend     = $false
    Data         = $false
    Dependencies = $false
}

$demoPatientPasswordPlain = $DemoPatientCredential.GetNetworkCredential().Password

Write-Info "Running pre-demo smoke checks..."

# 1) Backend health
$backendResult = Invoke-Api -Method "GET" -Uri "$BackendBaseUrl/actuator/health/readiness" -Timeout $TimeoutSec
if ($backendResult.ok -and $backendResult.data.status -eq "UP") {
    $summary.Backend = $true
    Write-Pass "Backend is running"
}
else {
    Write-Fail "Backend not reachable or readiness != UP" "run backend: Set-Location 'open-care-backend-dev'; & '.\\mvnw.cmd' @('-Dmaven.test.skip=true','spring-boot:run')"
}

# 2) Frontend availability
$responsiveFrontendPorts = @()
foreach ($port in @(3000, 3001)) {
    try {
        $resp = Invoke-WebRequest -Uri "http://localhost:$port" -Method Get -TimeoutSec $TimeoutSec -UseBasicParsing
        if ($resp.StatusCode -ge 200 -and $resp.StatusCode -lt 500) {
            $responsiveFrontendPorts += $port
        }
    }
    catch {
        # Try next port
    }
}

if ($responsiveFrontendPorts.Count -gt 0) {
    $summary.Frontend = $true
    Write-Pass "Frontend is reachable on port(s): $($responsiveFrontendPorts -join ', ')"
}
else {
    Write-Fail "Frontend not reachable" "run frontend: Set-Location 'open-care-frontend-dev'; yarn dev"
}

# 3) Port usage sanity + Docker dependencies
$dependenciesOk = $true

$backendListeners = Get-NetTCPConnection -LocalPort 6700 -State Listen -ErrorAction SilentlyContinue
if ($null -eq $backendListeners -or @($backendListeners).Count -ne 1) {
    $dependenciesOk = $false
    Write-Fail "Port 6700 is in broken/conflicting state" "keep exactly one backend process on 6700"
}
else {
    Write-Pass "Port 6700 listener is healthy"
}

$frontendListeners = @()
foreach ($port in @(3000, 3001)) {
    $listeners = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($listeners) {
        $frontendListeners += $port
    }
}

if ($frontendListeners.Count -eq 0) {
    $dependenciesOk = $false
    Write-Fail "No listener on frontend ports 3000/3001" "start frontend with yarn dev"
}
else {
    $stalePorts = $frontendListeners | Where-Object { $_ -notin $responsiveFrontendPorts }
    if ($stalePorts.Count -gt 0) {
        $dependenciesOk = $false
        Write-Fail "Stale frontend listeners detected on port(s): $($stalePorts -join ', ')" "stop stale process(es) and restart frontend"
    }
    else {
        Write-Pass "Frontend port state is healthy"
    }
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    $dependenciesOk = $false
    Write-Fail "Docker CLI not found" "install/start Docker Desktop"
}
else {
    $composeFile = Join-Path $PSScriptRoot "open-care-backend-dev/docker-compose.yml"
    if (-not (Test-Path $composeFile)) {
        $dependenciesOk = $false
        Write-Fail "Compose file not found" "run script from project root"
    }
    else {
        try {
            $runningServicesOutput = docker compose -f "$composeFile" ps --services --filter "status=running"
            $runningServices = @($runningServicesOutput -split "`n" | ForEach-Object { $_.Trim() } | Where-Object { $_ -ne "" })
            $requiredServices = @("postgres-app", "postgres-keycloak", "keycloak", "minio")
            $missingServices = $requiredServices | Where-Object { $_ -notin $runningServices }

            if ($missingServices.Count -gt 0) {
                $dependenciesOk = $false
                Write-Fail "Required Docker services missing: $($missingServices -join ', ')" "run: Set-Location 'open-care-backend-dev'; docker compose up -d postgres-app postgres-keycloak keycloak minio"
            }
            else {
                Write-Pass "Required Docker services are running"
            }
        }
        catch {
            $dependenciesOk = $false
            Write-Fail "Cannot query Docker services" "start Docker Desktop and rerun"
        }
    }
}

$summary.Dependencies = $dependenciesOk

# 4) Demo data checks
$dataOk = $true

if (-not $summary.Backend) {
    $dataOk = $false
    Write-Fail "Data checks skipped because backend is down" "start backend first"
}
else {
    # Approved doctor check
    $doctorResult = Invoke-Api -Method "GET" -Uri "$BackendBaseUrl/api/doctors?page=0&size=1" -Timeout $TimeoutSec
    $doctorCount = 0
    if ($doctorResult.ok) {
        $doctorCount = Get-TotalItems -Response $doctorResult.data
    }

    if ($doctorCount -ge 1) {
        Write-Pass "Approved doctor exists (count=$doctorCount)"
    }
    else {
        $dataOk = $false

        # Try to auto-create a pending doctor registration for easier fix path.
        $doctorRegisterBody = @{
            email                 = "demo.doctor@opencare.in"
            firstName             = "Demo"
            lastName              = "Doctor"
            phone                 = "9123456789"
            password              = "Demo@123A"
            bloodGroup            = "A_POSITIVE"
            gender                = "MALE"
            districtId            = $DemoDistrictId
            bmdcNo                = "DEMO-BMDC-001"
            degrees               = "MBBS"
            specializations       = "General Medicine"
            description           = "Demo doctor"
            consultationFeeOnline = 300
            consultationFeeOffline = 500
        }

        $doctorCreateResult = Invoke-Api -Method "POST" -Uri "$BackendBaseUrl/api/auth/register/doctor" -Body $doctorRegisterBody -Timeout $TimeoutSec
        if ($doctorCreateResult.ok) {
            Write-Info "Created pending demo doctor: demo.doctor@opencare.in"
        }

        Write-Fail "No approved doctor found for booking demo" "Step 1: open admin UI and approve doctor 'demo.doctor@opencare.in'. Step 2: rerun .\\demo-precheck.ps1"
    }

    # Patient existence check with default account (no env vars required)
    $patientToken = $null
    $patientLoginBody = @{
        username = $DemoPatientEmail
        password = $demoPatientPasswordPlain
    }

    $patientLoginResult = Invoke-Api -Method "POST" -Uri "$BackendBaseUrl/api/auth/login" -Body $patientLoginBody -Timeout $TimeoutSec

    if (-not $patientLoginResult.ok) {
        # Try creating default demo patient automatically, then login again.
        $patientRegisterBody = @{
            email      = $DemoPatientEmail
            firstName  = "Demo"
            lastName   = "Patient"
            phone      = "9876543210"
            password   = $demoPatientPasswordPlain
            bloodGroup = "A_POSITIVE"
            gender     = "MALE"
            districtId = $DemoDistrictId
        }

        $patientCreateResult = Invoke-Api -Method "POST" -Uri "$BackendBaseUrl/api/auth/register" -Body $patientRegisterBody -Timeout $TimeoutSec
        if ($patientCreateResult.ok) {
            Write-Info "Created demo patient: $DemoPatientEmail"
        }

        $patientLoginResult = Invoke-Api -Method "POST" -Uri "$BackendBaseUrl/api/auth/login" -Body $patientLoginBody -Timeout $TimeoutSec
    }

    if ($patientLoginResult.ok) {
        $patientToken = Get-FirstNotEmpty -Candidates @(
            $patientLoginResult.data.access_token,
            $patientLoginResult.data.accessToken,
            $patientLoginResult.data.token,
            $patientLoginResult.data.data.access_token,
            $patientLoginResult.data.data.accessToken,
            $patientLoginResult.data.data.token
        )
    }

    if (-not $patientToken) {
        $dataOk = $false
        Write-Fail "Patient demo account is not usable" "Run this exactly: 1) POST /api/auth/register with email '$DemoPatientEmail' and password '<demo password>' 2) rerun .\\demo-precheck.ps1"
    }
    else {
        $selfProfileResult = Invoke-Api -Method "GET" -Uri "$BackendBaseUrl/api/profiles/self" -Headers @{ Authorization = "Bearer $patientToken" } -Timeout $TimeoutSec
        if ($selfProfileResult.ok -and (Test-UserTypeIsPatient -UserType $selfProfileResult.data.userType)) {
            Write-Pass "Patient check passed ($DemoPatientEmail)"
        }
        else {
            $dataOk = $false
            Write-Fail "Logged-in demo account is not a patient" "Use a patient account credentials and rerun. Current default account: $DemoPatientEmail"
        }
    }
}

$summary.Data = $dataOk

Write-Host ""
Write-Host "===== PRE-DEMO SUMMARY =====" -ForegroundColor Cyan
foreach ($key in @("Backend", "Frontend", "Data", "Dependencies")) {
    $statusText = if ($summary[$key]) { "PASS" } else { "FAIL" }
    $color = if ($summary[$key]) { "Green" } else { "Red" }
    Write-Host ("{0,-13}: {1}" -f $key, $statusText) -ForegroundColor $color
}

if ($summary.Backend -and $summary.Frontend -and $summary.Data -and $summary.Dependencies) {
    Write-Host ""
    Write-Host ("{0} SYSTEM READY FOR DEMO" -f [char]0x2705) -ForegroundColor Green
    exit 0
}
else {
    Write-Host ""
    Write-Host ("{0} DEMO NOT READY" -f [char]0x274C) -ForegroundColor Red
    exit 1
}
