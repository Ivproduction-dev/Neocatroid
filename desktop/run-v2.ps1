param(
    [Parameter(Mandatory = $true)][string]$Project,
    [string]$Variant = "catroidDebug",
    [string]$CompileTask = "compileCatroidDebugJavaWithJavac",
    [string]$ProcessTask = "processCatroidDebugResources",
    [string]$MainClass = "org.catrobat.catroid.desktop.DesktopModelKt",
    [switch]$Visible
)

$SafeProject = $Project
if ($Project -match '[^\x00-\x7F]') {
    $stage = "$env:TEMP\neocatroid-proj"
    Remove-Item -Recurse -Force $stage -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Force -Path $stage | Out-Null
    Copy-Item $Project (Join-Path $stage "project.newtrobat") -Force
    $SafeProject = Join-Path $stage "project.newtrobat"
}

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$jdkJava = "C:\Users\ivanp\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"

.\gradlew.bat ":desktop:build" -x test --offline
if ($LASTEXITCODE -ne 0) { exit 1 }

function Find-Jars([string]$groupPath, [string]$filter) {
    Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\$groupPath" -Recurse -Filter $filter -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty FullName
}

function Find-Jar([string]$groupPath, [string]$name) {
    Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\$groupPath" -Recurse -Filter $name |
        Select-Object -First 1 -ExpandProperty FullName
}

function Find-Jars([string]$groupPath, [string]$filter) {
    Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\$groupPath" -Recurse -Filter $filter -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty FullName
}

$lwjglJars = @()
$lwjglJars += Find-Jars "com.badlogicgames.gdx\gdx-backend-lwjgl3" "gdx-backend-lwjgl3-1.13.1.jar"
$lwjglJars += Find-Jars "com.badlogicgames.gdx\gdx-freetype" "gdx-freetype-1.13.1.jar"
$lwjglJars += Find-Jars "com.badlogicgames.gdx\gdx-freetype-platform" "*.jar" | Where-Object { $_ -notmatch "sources|javadoc" }
$lwjglJars += Find-Jars "com.squareup.okhttp3\okhttp" "okhttp-4.12.0.jar"
$lwjglJars += Find-Jars "com.squareup.okio" "*.jar" | Where-Object { $_ -notmatch "sources|javadoc" }
$lwjglJars += Find-Jars "org.lwjgl" "*.jar" | Where-Object { $_ -notmatch "sources|javadoc" }

$androidxJars = Get-ChildItem "$env:USERPROFILE\.gradle\caches\8.12\transforms" -Recurse -Filter *.jar -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -match '\\transformed\\' -and $_.Name -notmatch '-api\.jar$|-lint\.jar|inspector\.jar' -and $_.FullName -notmatch 'espresso|uiautomator|robolectric|junit|hamcrest|mockito|runner|rules-|monitor-|test-' } |
    Select-Object -ExpandProperty FullName

$cp = @(
    "$root\desktop\build\libs\desktop.jar",
    "$root\core\build\libs\core.jar",
    "$root\catroid\build\intermediates\javac\$Variant\$CompileTask\classes",
    "$root\catroid\build\tmp\kotlin-classes\$Variant",
    "$root\catroid\build\intermediates\compile_and_runtime_not_namespaced_r_class_jar\$Variant\$ProcessTask\R.jar",
    (Find-Jar "org.jetbrains.kotlin\kotlin-stdlib" "kotlin-stdlib-2.0.21.jar"),
    (Find-Jar "com.badlogicgames.gdx\gdx" "gdx-1.13.1.jar"),
    (Find-Jar "com.thoughtworks.xstream\xstream" "xstream-1.4.20.jar"),
    (Find-Jar "io.github.x-stream\mxparser" "mxparser-1.2.2.jar"),
    (Find-Jar "xmlpull\xmlpull" "xmlpull-1.1.3.1.jar"),
    (Find-Jar "com.google.guava\guava\33.4.0-android" "guava-33.4.0-android.jar")
) + $lwjglJars + $androidxJars -join ";"

$argFile = "$env:TEMP\neocatroid-v2-args.txt"
$escapedCp = ($cp -join ";").Replace("\", "\\")
"-cp `"$escapedCp`" $MainClass `"$($SafeProject.Replace('\', '\\'))`"" | Out-File -Encoding ascii -FilePath $argFile
if ($Visible) {
    $bat = "$env:TEMP\neocatroid-run.bat"
    "`"$jdkJava`" `"@$argFile`" %*" | Out-File -Encoding ascii -FilePath $bat
    Start-Process cmd -ArgumentList "/k `"$bat`""
} else {
    & $jdkJava "@$argFile"
}
