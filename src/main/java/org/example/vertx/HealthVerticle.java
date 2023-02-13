package org.example.vertx;

import io.vertx.core.Promise;
import io.vertx.rxjava3.core.AbstractVerticle;

public class HealthVerticle extends AbstractVerticle {

  public static final String HEALTH_ADDRESS = "EVENT.BUS.HEALTH.ADDRESS";

  @Override
  public void start(final Promise<Void> startFuture) {
    vertx.eventBus()
        .consumer(HEALTH_ADDRESS)
        .toObservable()
        .doOnNext(message -> message.reply("Success"))
        .subscribe(
            message -> System.out.println("Handled successfully."),
            Throwable::printStackTrace,
            () -> System.out.println("Finished successfully.")
        );

    startFuture.complete();
  }
}
