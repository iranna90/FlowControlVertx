package org.example.config;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.Vertx;

public class ConfigLoader {
  public static ConfigRetriever getConfigRetriever(final Vertx vertx) {
    final var fileStore = new ConfigStoreOptions()
        .setType("file")
        .setFormat("properties")
        .setConfig(new JsonObject().put("path", "application.properties"));

    final var envStore = new ConfigStoreOptions()
        .setType("env");

    final var configOptions = new ConfigRetrieverOptions()
        .addStore(fileStore)
        .addStore(envStore);

    return ConfigRetriever
        .create(vertx, configOptions);
  }
}
