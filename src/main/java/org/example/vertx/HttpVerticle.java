package org.example.vertx;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.eventbus.Message;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;

import static io.vertx.core.http.HttpMethod.GET;

public class HttpVerticle extends AbstractVerticle {

  private static final AtomicInteger currentActiveRequests = new AtomicInteger(0);

  private int maxAllowedActiveRequestCount;

  @Override
  public void start(final Promise<Void> startFuture) {
    maxAllowedActiveRequestCount = config().getInteger("APP_MAX_CONCURRENT_REQUESTS");

    final var server = vertx.createHttpServer();
    final var router = Router.router(vertx);

    // health as the first route
    router.route().method(GET).path("/health/ready").handler(this::handleHealth);

    // flow control
    router.route().handler(this::countActiveRequestsHandler);

    // license handling
    router.route().method(GET).path("/license").handler(this::handleLicense);

    server.requestHandler(router)
        .rxListen(config().getInteger("APP_PORT"))
        .subscribe(
            success -> startFuture.complete(),
            startFuture::fail
        );
  }

  private void handleLicense(final RoutingContext routingContext) {
    vertx.eventBus()
        .<String>request(LicenseVerticle.LICENSE_ADDRESS, "any")
        .map(Message::body)
        .subscribe(
            success -> routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end(),
            error -> routingContext.response().setStatusCode(HttpResponseStatus.SERVICE_UNAVAILABLE.code()).end()
        );
  }

  private void handleHealth(final RoutingContext routingContext) {
    vertx.eventBus()
        .request(HealthVerticle.HEALTH_ADDRESS, "any")
        .subscribe(
            success -> routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end(),
            error -> routingContext.response().setStatusCode(HttpResponseStatus.SERVICE_UNAVAILABLE.code()).end()
        );
  }

  private void countActiveRequestsHandler(final RoutingContext routingContext) {
    if (currentActiveRequests.get() >= maxAllowedActiveRequestCount) {
      System.out.println("Reached max allowed limit");
      routingContext.response().setStatusCode(HttpResponseStatus.SERVICE_UNAVAILABLE.code()).end();
    } else {
      // when the request is finished, end handler will decrement the active count.
      routingContext.response().endHandler(any -> decrementActiveRequestCount());
      int activeCount = currentActiveRequests.incrementAndGet();
      System.out.println("Current active is " + activeCount);
      routingContext.next();
    }
  }

  private void decrementActiveRequestCount() {
    currentActiveRequests.decrementAndGet();
  }
}
