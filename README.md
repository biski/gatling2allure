# allure-gatling
Convert Gatling log to Allure report

# Configuration
## logback.xml in Gatling project
<!-- Uncomment for logging ALL HTTP request and responses -->
<logger name="io.gatling.http.ahc" level="TRACE" />
<logger name="io.gatling.http.response" level="TRACE" />
