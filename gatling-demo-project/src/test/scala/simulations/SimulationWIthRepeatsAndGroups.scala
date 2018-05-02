package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class SimulationWIthRepeatsAndGroups extends Simulation {

    val protocol = http
        .baseURL("http://computer-database.gatling.io")
        .acceptHeader("application/xml, text/html, text/plain, application/json, */*")
        .acceptCharsetHeader("UTF-8")
        .acceptEncodingHeader("gzip, deflate")

    val headers = Map(
        "Accept" -> "application/xml, text/html, text/plain, application/json, */*",
        "Accept-Encoding" -> "gzip, deflate")


    val scn = scenario("Scenario with group and repeat inside")
      .exec(
        http("request before group")
          .get("/")
      )
      .group("group name") {
        repeat(5) {
              exec(
                  http("request in repeat")
                    .get("/")
              )
          }
      }
      .exec(
        http("request after group")
          .get("/")
      )

    setUp(
        scn.inject(atOnceUsers(1))
    ).protocols(protocol)
}