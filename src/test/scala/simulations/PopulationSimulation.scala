import io.gatling.core.Predef.*
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder

import scala.concurrent.duration.*

class ReqResSimulation extends Simulation {

  // Définission du protocole HTTP
  val httpProtocol = http
    .baseUrl("https://reqres.in") 
    .acceptHeader("application/json") 
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling/3.0") 

  // Test avec une méthode POST
  val addUsersRequest: HttpRequestBuilder = http("Ajouter un utilisateur")
    .post("/api/users")
    .body(StringBody(
      """{
        |  "name": "morpheus",
        |  "job": "leader"
        |}""".stripMargin
    )).asJson
    .check(
      status.is(201), 
      jsonPath("$.name").is("morpheus"), 
      jsonPath("$.job").is("leader")
    )


  // Test avec une méthode GET
  val getUsersRequest: HttpRequestBuilder = http("Récupérer les utilisateurs de page 2")
    .get("/api/users?page=2") 
    .check(
      status.is(200),
      jsonPath("$.page").is("2"), 
      jsonPath("$.per_page").is("6"),
      jsonPath("$.total").is("12"),
      jsonPath("$.total_pages").is("2"),
      jsonPath("$.data[0].email").is("michael.lawson@reqres.in")
    )

  // Définir le scénario
  val scn = scenario("Get Users API Test")
    .exec(addUsersRequest) 
    .pause(1)
    .exec(getUsersRequest)

  // Configuration de la simulation avec plusieurs types d'injection
  setUp(
    scn.inject(
      // Injection basique : 10 utilisateurs simultanés
      atOnceUsers(10),

      // Injection progressive : Augmente les utilisateurs progressivement
      rampUsers(20) during (10.seconds),

      // Injection constante : Maintient une charge constante
      constantUsersPerSec(5) during (20.seconds),

      // Injection avec augmentation progressive de la charge
      rampUsersPerSec(1) to 10 during (15.seconds),

      // Injection en paliers : Augmente par paliers
      incrementUsersPerSec(5)
        // Pendant 3 étapes
        .times(3)
        // Chaque niveau dure 10 secondes
        .eachLevelLasting(10.seconds)
        // Commence avec 2 utilisateurs par seconde
        .startingFrom(2)
    )
  ).protocols(httpProtocol)
}
