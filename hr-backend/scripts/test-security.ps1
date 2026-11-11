$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot '..')
try {
    mvn -o package -q
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed' }
    $servletJar = Join-Path $env:USERPROFILE '.m2/repository/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar'
    $securityClasspath = "target/classes;target/test-classes;target/hr-backend-1.0-SNAPSHOT/WEB-INF/lib/*;$servletJar"
    java -cp $securityClasspath net.xju.security.SecurityRegressionTest
    if ($LASTEXITCODE -ne 0) { throw 'Security regression failed' }
    python scripts/test_salary_guards.py
    if ($LASTEXITCODE -ne 0) { throw 'Salary regression failed' }
} finally { Pop-Location }
