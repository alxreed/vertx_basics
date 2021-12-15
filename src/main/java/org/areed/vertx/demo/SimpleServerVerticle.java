package org.areed.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

public class SimpleServerVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> promise) {

    var router = Router.router(vertx);
    router.get("/api/dogs/dog/:id")
        .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
        .handler(this::getDog);

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(config().getInteger("http.port", 9090),
            result -> {
              if (result.succeeded()) {
                promise.complete();
              } else {
                promise.fail(result.cause());
              }
            });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SimpleServerVerticle());
  }

  private void getDog(RoutingContext routingContext) {
    var dogId = routingContext.request()
        .getParam("id");
    var dog = new Dog(
        dogId,
        "Titus",
        "Mixed Husky, Labrador retriever"
    );

    routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(200)
        .end(Json.encodePrettily(dog));
  }
}
