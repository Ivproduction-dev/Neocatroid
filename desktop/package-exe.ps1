param(
    [string]$Jdk = "C:\Users\ivanp\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.19.10-hotspot",
    [string]$OutRoot = "$PSScriptRoot\build\exe"
)

$ErrorActionPreference = "Stop"
$inDir = "$OutRoot\input"
$rtDir = "$OutRoot\runtime"
$appDir = "$OutRoot\app"
Remove-Item -Recurse -Force $inDir, $rtDir, $appDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $inDir | Out-Null

.\gradlew.bat ":desktop:jar" --offline
if ($LASTEXITCODE -ne 0) { exit 1 }

Copy-Item desktop\build\libs\desktop.jar $inDir -Force
$stdlib = Get-ChildItem ~/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/2.0.21/*/kotlin-stdlib-2.0.21.jar |
    Select-Object -First 1 -ExpandProperty FullName
Copy-Item $stdlib $inDir -Force
$annotations = Get-ChildItem ~/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/*/annotations-*.jar -Recurse |
    Select-Object -First 1 -ExpandProperty FullName
if ($annotations) { Copy-Item $annotations $inDir -Force }

& "$Jdk\bin\jlink.exe" --add-modules java.base,java.xml,java.logging --output $rtDir --strip-debug --no-man-pages --no-header-files
& "$Jdk\bin\jpackage.exe" --type app-image --name NeoCatroidDesktop `
    --input $inDir --main-jar desktop.jar `
    --main-class org.catrobat.catroid.desktop.DesktopMainKt `
    --runtime-image $rtDir --dest $appDir --verbose
