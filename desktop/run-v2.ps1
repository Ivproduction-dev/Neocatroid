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

$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot
$jdkJava = "C:\Users\ivanp\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"

.\gradlew.bat ":desktop:build" -x test --offline
if ($LASTEXITCODE -ne 0) { exit 1 }

function Find-Jars([string]$groupPath, [string]$filter) {
    Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\$groupPath" -Recurse -Filter $filter -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty FullName
}

$androidxJars = Get-ChildItem "$env:USERPROFILE\.gradle\caches\8.12\transforms" -Recurse -Filter *.jar -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -match '\\transformed\\' -and $_.Name -notmatch '-api\.jar$|-lint\.jar|inspector\.jar' -and $_.FullName -notmatch 'espresso|uiautomator|robolectric|junit|hamcrest|mockito|runner|rules-|monitor-|test-' } |
    Select-Object -ExpandProperty FullName

$cpLines = & .\gradlew.bat -q ":desktop:printRuntimeClasspath" --offline 2>$null | Where-Object { $_ -and $_.Length -gt 200 }
$gradleCp = ($cpLines | Sort-Object Length -Descending | Select-Object -First 1)
if (-not $gradleCp) {
    Write-Error "Gradle did not return a runtime classpath for :desktop"
    exit 1
}

$androidJar = Get-ChildItem "$env:LOCALAPPDATA\Android\Sdk\platforms" -Directory -ErrorAction SilentlyContinue |
    ForEach-Object {
        $m = [regex]::Match($_.Name, '^android-(\d+)(?:\.(\d+))?$')
        if ($m.Success -and (Test-Path (Join-Path $_.FullName 'android.jar'))) {
            [PSCustomObject]@{ Api = [int]$m.Groups[1].Value; Sub = [int]$(if ($m.Groups[2].Success) { $m.Groups[2].Value } else { 0 }); Path = Join-Path $_.FullName 'android.jar' }
        }
    } |
    Sort-Object Api, Sub -Descending |
    Select-Object -First 1 -ExpandProperty Path

if (-not $androidJar) {
    Write-Error "android.jar not found in the Android SDK"
    exit 1
}

$cp = @(
    $gradleCp,
    "$root\catroid\build\intermediates\javac\$Variant\$CompileTask\classes",
    "$root\catroid\build\tmp\kotlin-classes\$Variant",
    "$root\catroid\build\intermediates\compile_and_runtime_not_namespaced_r_class_jar\$Variant\$ProcessTask\R.jar",
    (Find-Jars "com.google.guava\guava" "guava-33.4.0-android.jar")
) + $androidxJars + $androidJar

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
