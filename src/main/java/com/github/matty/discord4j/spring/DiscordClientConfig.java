package com.github.matty.discord4j.spring;

import discord4j.core.GatewayDiscordClient;

public abstract class DiscordClientConfig {

    protected void startAwaitThread(final GatewayDiscordClient gatewayDiscordClient) {
        Thread awaitThread = new Thread("discord") {
            @Override
            public void run() {
                gatewayDiscordClient.onDisconnect().block();
            }
        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
