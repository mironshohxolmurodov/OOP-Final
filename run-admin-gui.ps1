$ErrorActionPreference = "Stop"

$javaFxPath = Join-Path $PSScriptRoot "lib\javafx"
if (!(Test-Path $javaFxPath)) {
    throw "JavaFX libraries were not found at $javaFxPath"
}

Push-Location $PSScriptRoot
try {
    & (Join-Path $PSScriptRoot "compile-admin-gui.ps1")
    $buildPath = Join-Path $PSScriptRoot "build\classes"
    $arguments = @(
        '--enable-native-access=javafx.graphics',
        '--module-path', $javaFxPath,
        '--add-modules', 'javafx.controls',
        '-cp', $buildPath,
        'AdminFxLauncher'
    )
    Start-Process -FilePath 'javaw' -ArgumentList $arguments -WorkingDirectory $PSScriptRoot
}
finally {
    Pop-Location
}
