package com.messi.rbm.simulator.gatling;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

/**
 * Simulación de carga para envío masivo de mensajes al simulador RBM.
 */
public class AgentMessagesSimulation extends Simulation {

  private final HttpProtocolBuilder httpProtocol =
      http.baseUrl("http://localhost:8080").acceptHeader("application/json");

  private final ChainBuilder getToken =
      exec(
          http("get-token")
              .post("/token")
              .header("Content-Type", "application/x-www-form-urlencoded")
              .formParam("grant_type", "client_credentials")
              .formParam("client_id", "test")
              .formParam("client_secret", "test")
              .check(jsonPath("$.access_token").saveAs("token")));

  private final ChainBuilder sendMessage =
      exec(
          http("send-message")
              .post(
                  "/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&messageId=#{randomUuid()}")
              .header("Authorization", "Bearer #{token}")
              .header("Content-Type", "application/json")
              .body(StringBody("{\"contentMessage\":{\"text\":\"Hola desde Gatling\"}}"))
              .check(status().is(200)));

  private final ScenarioBuilder scn =
      scenario("AgentMessagesLoadTest").exec(getToken).exec(sendMessage);

  {
    setUp(scn.injectOpen(rampUsers(10).during(10))).protocols(httpProtocol);
  }
}

