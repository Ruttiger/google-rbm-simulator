package com.messi.rbm.simulator.gatling;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.repeat;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simulación de carga para envío masivo de mensajes al simulador RBM.
 */
public class AgentMessagesSimulation extends Simulation {

  private String token;

  private final HttpProtocolBuilder httpProtocol =
      http.baseUrl("http://localhost:8080").acceptHeader("application/json");

  private final ChainBuilder setToken = exec(session -> session.set("token", token));

  private final ChainBuilder sendMessage =
      exec(
          http("send-message")
              .post(
                  "/v1/phones/+5215512345678/agentMessages?agentId=AGENT_ID&messageId=#{randomUuid()}")
              .header("Authorization", "Bearer #{token}")
              .header("Content-Type", "application/json")
              .body(StringBody("{\"contentMessage\":{\"text\":\"Hola desde Gatling\"}}"))
              .check(status().is(200)));

  private final int threads = Integer.getInteger("threads", 10);
  private final int messages = Integer.getInteger("messages", 1);

  private final ScenarioBuilder scn =
      scenario("AgentMessagesLoadTest").exec(setToken).repeat(messages).on(sendMessage);

  @Override
  public void before() {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "grant_type=client_credentials&client_id=test&client_secret=test"))
            .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(response.body());
      token = node.get("access_token").asText();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  {
    setUp(scn.injectOpen(rampUsers(threads).during(10))).protocols(httpProtocol);
  }
}
