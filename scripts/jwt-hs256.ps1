param(
  [string]$Secret = $env:JWT_HMAC_SECRET,
  [string]$Subject = "user1",
  [int]$TtlMinutes = 60
)

if (-not $Secret) {
  Write-Error "JWT secret is required. Pass -Secret or set JWT_HMAC_SECRET."
  exit 1
}

if ($Secret.Length -lt 32) {
  Write-Error "JWT secret must be at least 32 characters (256 bits) for HS256."
  exit 1
}

function Base64Url([byte[]]$bytes) {
  [Convert]::ToBase64String($bytes).TrimEnd('=').Replace('+','-').Replace('/','_')
}

$now = [DateTimeOffset]::UtcNow
$iat = [int]$now.ToUnixTimeSeconds()
$exp = [int]$now.AddMinutes($TtlMinutes).ToUnixTimeSeconds()

$header = @{ alg = "HS256"; typ = "JWT" } | ConvertTo-Json -Compress
$payload = @{
  sub = $Subject
  iat = $iat
  exp = $exp
} | ConvertTo-Json -Compress

$h64 = Base64Url([Text.Encoding]::UTF8.GetBytes($header))
$p64 = Base64Url([Text.Encoding]::UTF8.GetBytes($payload))
$data = "$h64.$p64"

$hmac = [System.Security.Cryptography.HMACSHA256]::new(
  [Text.Encoding]::UTF8.GetBytes($Secret)
)
$sig = Base64Url($hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($data)))

Write-Output "$data.$sig"
