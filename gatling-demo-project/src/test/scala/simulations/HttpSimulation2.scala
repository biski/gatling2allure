package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class HttpSimulation2 extends Simulation {

    val protocol = http
        .baseURL("http://computer-database.gatling.io")
        .acceptHeader("application/xml, text/html, text/plain, application/json, */*")
        .acceptCharsetHeader("UTF-8")
        .acceptEncodingHeader("gzip, deflate")

    val headers = Map(
        "Accept" -> "application/xml, text/html, text/plain, application/json, */*",
        "Accept-Encoding" -> "gzip, deflate")


    val scn = scenario("Scenario2")
        .exec(
            http("Login and Post Data")
                .post("/computers")
                .body(StringBody("Example body"))
                .headers(headers)
                .queryParam("login", "admin")
                .queryParam("password", "secret")
        )

    setUp(
        scn.inject(atOnceUsers(1))
    ).protocols(protocol)
}