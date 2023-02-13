package org.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import org.example.config.ConfigLoader;
import org.example.vertx.HealthVerticle;
import org.example.vertx.HttpVerticle;
import org.example.vertx.LicenseVerticle;

public class Main extends AbstractVerticle {
  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    ConfigLoader.getConfigRetriever(vertx)
        .rxGetConfig()
        .map(config -> new DeploymentOptions().setConfig(config))
        .flatMap(deploymentOptions -> vertx.rxDeployVerticle(new LicenseVerticle(), deploymentOptions).map(id -> deploymentOptions))
        .flatMap(deploymentOptions -> vertx.rxDeployVerticle(new HealthVerticle(), deploymentOptions).map(id -> deploymentOptions))
        .flatMap(deploymentOptions -> vertx.rxDeployVerticle(new HttpVerticle(), deploymentOptions))
        .subscribe(
            success -> System.out.println("Deployed successfully."),
            error -> {
              error.printStackTrace();
              System.exit(400);
            }
        );
  }
}