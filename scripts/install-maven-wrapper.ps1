Param()

$dest = Join-Path -Path $PSScriptRoot -ChildPath "..\.mvn\wrapper"
New-Item -ItemType Directory -Path $dest -Force | Out-Null

$url = "https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"
$target = Join-Path $dest "maven-wrapper.jar"
Write-Host "Downloading maven-wrapper.jar to $target"
try {
    Invoke-WebRequest -Uri $url -OutFile $target -UseBasicParsing -ErrorAction Stop
    Write-Host "Downloaded maven-wrapper.jar"
} catch {
    Write-Error "Failed to download maven-wrapper.jar: $_"
    exit 1
}
