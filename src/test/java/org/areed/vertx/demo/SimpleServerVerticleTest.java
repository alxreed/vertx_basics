package org.areed.vertx.demo;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@ExtendWith(VertxExtension.class)
class SimpleServerVerticleTest {

  @BeforeAll
  static void setup(Vertx vertx,
                    VertxTestContext testContext) {
    vertx.deployVerticle(new SimpleServerVerticle(), testContext.succeeding(s ->
        testContext.completeNow()));
  }

  @Test
  void getOneDog(Vertx vertx,
                 VertxTestContext testContext) {
    final WebClient webClient = WebClient.create(vertx);

    webClient.get(9090, "localhost", "/api/dogs/dog/10")
        .as(BodyCodec.jsonObject())
        .send(testContext.succeeding(response -> {
              testContext.verify(() ->
                  Assertions.assertAll(
                      () -> Assertions.assertEquals(200, response.statusCode()),
                      () -> Assertions.assertEquals(readFileAsJsonObject("src/test/resources/getOneDog/response.json"), response.body())
                  )
              );
              testContext.completeNow();
            })
        );
  }

  private JsonObject readFileAsJsonObject(String path) throws IOException {
    return new JsonObject(Files.lines(Paths.get(path), StandardCharsets.UTF_8).collect(Collectors.joining("\n")));
  }
}