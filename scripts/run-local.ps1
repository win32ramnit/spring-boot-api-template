$env:SPRING_PROFILES_ACTIVE = "dev"
if (-not $env:DB_URL) { $env:DB_URL = "jdbc:mysql://localhost:3306/template_dev" }
if (-not $env:DB_USERNAME) { $env:DB_USERNAME = "root" }
if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = "root!" }

.\gradlew.bat bootRun
