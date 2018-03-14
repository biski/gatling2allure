# Gatling2Allure converter
Tired of analyzing Gatling text logs to extract useful information?
Convert Gatling log to beautiful [Allure](https://github.com/allure-framework/allure2) report.
See [example Allure report](https://ci.qameta.in/job/allure2/job/master/Demo2_Report/)

# Configuration
## logback.xml in Gatling project
Ensure that Gatling logs passed and failed requests with responses to file.
 Change logback.xml configuration:
```xml
<!-- Uncomment for logging ALL HTTP request and responses -->
<logger name="io.gatling.http.ahc" level="TRACE" />
<logger name="io.gatling.http.response" level="TRACE" />
```

# Run
- run Gatling tests
- run gatling2allure converter:
```bash
java -jar <jar_name> /path/to/gatling.log
```
- generate [Allure](https://github.com/allure-framework/allure2) report
- open report in Firefox


