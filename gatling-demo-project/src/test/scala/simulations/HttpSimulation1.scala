package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

class HttpSimulation1 extends Simulation {
  val protocol: HttpProtocolBuilder = http
    .baseURL("http://computer-database.gatling.io")

  val scn = scenario("Scenario1")
    .exec(
      http("myRequest1")
        .get("/")
    )


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(protocol)
}