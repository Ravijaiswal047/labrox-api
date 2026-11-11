# LabRox Enterprise LIMS - End-to-End Clinical Smoke Test
param (
    [string]$GatewayUrl = "http://localhost:8088"
)

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "LabRox LIMS End-to-End API Smoke Test Suite" -ForegroundColor Cyan
Write-Host "Gateway Target: $GatewayUrl" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan

$passed = 0
$failed = 0

function Test-Endpoint {
    param (
        [string]$Name,
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers = @{},
        [string]$Body = $null
    )

    Write-Host -NoNewline "[TEST] $Name ... "
    try {
        $params = @{
            Uri = $Uri
            Method = $Method
            ContentType = "application/json"
            TimeoutSec = 10
        }
        if ($Headers.Count -gt 0) { $params.Headers = $Headers }
        if ($Body) { $params.Body = $Body }

        $res = Invoke-RestMethod @params
        Write-Host "PASS [200 OK]" -ForegroundColor Green
        $script:passed++
        return $res
    } catch {
        Write-Host "FAIL [$($_.Exception.Message)]" -ForegroundColor Red
        $script:failed++
        return $null
    }
}

# 1. Gateway Actuator Health
Test-Endpoint -Name "Gateway Actuator Health" -Method "GET" -Uri "$GatewayUrl/actuator/health"

# 2. IAM Step 1: LoginWithOTP
$loginOtpBody = '{"UserName": "admin"}'
Test-Endpoint -Name "IAM LoginWithOTP" -Method "POST" -Uri "$GatewayUrl/api/v1/Login/LoginWithOTP" -Body $loginOtpBody

# 3. IAM Step 2: Full Login & JWT Generation
$loginBody = '{"UserName": "admin", "Password": "Admin@123"}'
$authRes = Test-Endpoint -Name "IAM Authenticate & Token" -Method "POST" -Uri "$GatewayUrl/api/v1/Login/login" -Body $loginBody

$token = ""
if ($authRes -and $authRes.token) {
    $token = $authRes.token
} elseif ($authRes -and $authRes.message -and $authRes.message.token) {
    $token = $authRes.message.token
}
$authHeaders = @{}
if ($token) {
    $authHeaders["Authorization"] = "Bearer $token"
}

# 4. Navigation Menu Tree
Test-Endpoint -Name "IAM MainMenuPageData" -Method "GET" -Uri "$GatewayUrl/api/v1/Menu/MainMenuPageData" -Headers $authHeaders

# 5. Centers Dropdown
Test-Endpoint -Name "IAM GetCentreList" -Method "GET" -Uri "$GatewayUrl/api/v1/Centre/GetCentreList" -Headers $authHeaders

# 6. Test Catalog Investigations
Test-Endpoint -Name "Catalog GetInvestigations" -Method "GET" -Uri "$GatewayUrl/api/v1/Investigations/GetInvestigations" -Headers $authHeaders

# 7. Phlebotomy Sample Collection
$scBody = '{"CentreID": 1, "FromDate": "01-Jan-2026", "ToDate": "31-Dec-2026"}'
Test-Endpoint -Name "Phlebotomy GetSampleCollection" -Method "POST" -Uri "$GatewayUrl/api/v1/SC/GetSampleCollection" -Headers $authHeaders -Body $scBody

# 8. Diagnostic Result Entry
$reBody = '{"CentreID": 1, "DepartmentID": 1}'
Test-Endpoint -Name "Diagnostic GetResultEntry" -Method "POST" -Uri "$GatewayUrl/api/v1/RE/GetResultEntry" -Headers $authHeaders -Body $reBody

# 9. Doctor Approval Patient Search
$approvalBody = '{"CentreID": 1}'
Test-Endpoint -Name "Doctor Approval PatientSearch" -Method "POST" -Uri "$GatewayUrl/api/v1/DoctorApproval/PatientSearch" -Headers $authHeaders -Body $approvalBody

# 10. Clinical PDF Doctor Report
$reportPayload = '{"TestID": 1001, "LedgerTransactionNo": "LT2026001"}'
Test-Endpoint -Name "Reporting DoctorReport" -Method "POST" -Uri "$GatewayUrl/api/v1/Reports/DoctorReport" -Headers $authHeaders -Body $reportPayload

# 11. Work Order & Barcode Queue
Test-Endpoint -Name "Orders WorkSheetOrder" -Method "POST" -Uri "$GatewayUrl/api/v1/WorkSheetOrder/GetWorkSheetOrder" -Headers $authHeaders -Body '{"CentreID": 1}'

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Smoke Test Results: $passed Passed, $failed Failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "===============================================================================" -ForegroundColor Cyan
