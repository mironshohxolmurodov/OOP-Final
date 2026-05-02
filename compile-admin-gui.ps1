$ErrorActionPreference = "Stop"

$javaFxPath = Join-Path $PSScriptRoot "lib\javafx"
if (!(Test-Path $javaFxPath)) {
    throw "JavaFX libraries were not found at $javaFxPath"
}

$buildPath = Join-Path $PSScriptRoot "build\classes"
New-Item -ItemType Directory -Force -Path $buildPath | Out-Null

$sources = @(Get-ChildItem -Path $PSScriptRoot -Filter *.java -File | ForEach-Object { $_.FullName })
$sources += @(Get-ChildItem -Path (Join-Path $PSScriptRoot "JavaFX_Admin_Panel") -Filter *.java -File | ForEach-Object { $_.FullName })

$classFiles = @(Get-ChildItem -Path $buildPath -Recurse -Filter *.class -File -ErrorAction SilentlyContinue)
$needsCompile = $classFiles.Count -eq 0

if (-not $needsCompile) {
    $latestSourceWrite = ($sources | ForEach-Object { (Get-Item $_).LastWriteTimeUtc.Ticks } | Measure-Object -Maximum).Maximum
    $latestClassWrite = ($classFiles | ForEach-Object { $_.LastWriteTimeUtc.Ticks } | Measure-Object -Maximum).Maximum
    $needsCompile = $latestSourceWrite -gt $latestClassWrite
}

if ($needsCompile) {
    javac -d $buildPath --module-path $javaFxPath --add-modules javafx.controls $sources
}
