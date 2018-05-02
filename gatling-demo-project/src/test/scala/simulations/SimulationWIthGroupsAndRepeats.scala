package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class SimulationWIthGroupsAndRepeats extends Simulation {

  val protocol = http
    .baseURL("http://computer-database.gatling.io")
    .acceptHeader("application/xml, text/html, text/plain, application/json, */*")
    .acceptCharsetHeader("UTF-8")
    .acceptEncodingHeader("gzip, deflate")

  val headers = Map(
    "Accept" -> "application/xml, text/html, text/plain, application/json, */*",
    "Accept-Encoding" -> "gzip, deflate")


  val scn = scenario("Scenario with repeat and group inside")
    .exec(
      http("request before group")
        .get("/")
    )
    .repeat(5) {
      group("group name") {
        exec(
          http("request in group")
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