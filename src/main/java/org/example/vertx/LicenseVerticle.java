package org.example.vertx;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Promise;
import io.vertx.rxjava3.core.AbstractVerticle;

public class LicenseVerticle extends AbstractVerticle {

  public static final String LICENSE_ADDRESS = "EVENT.BUS.LICENSE.ADDRESS";

  @Override
  public void start(final Promise<Void> startFuture) throws Exception {
    vertx.eventBus()
        .<String>consumer(LICENSE_ADDRESS)
        .toObservable()
        .delay(1, TimeUnit.SECONDS)
        .doOnNext(message -> message.reply("Success"))
        .subscribe(
            message -> System.out.println("Handled successfully."),
            Throwable::printStackTrace,
            () -> System.out.println("Finished successfully.")
        );

    startFuture.complete();
  }
}
