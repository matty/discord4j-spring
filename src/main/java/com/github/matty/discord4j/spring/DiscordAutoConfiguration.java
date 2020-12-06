package com.github.matty.discord4j.spring;

import com.github.matty.discord4j.spring.annotations.EnableDiscord;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.GatewayOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Auto-configuration for {@link DiscordClient}.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Configuration
@Import(DiscordTokenAutoConfiguration.class)
@ConditionalOnBean(annotation = EnableDiscord.class)
public class DiscordAutoConfiguration {

    private final DiscordClient discordClient;

    @Autowired
    public DiscordAutoConfiguration(DiscordTokenProvider tokenProvider) {
        this.discordClient = DiscordClientBuilder.create(tokenProvider.getToken()).build();
    }

    @Bean("eventDispatcher")
    public EventDispatcher eventDispatcher() {
        return EventDispatcher.builder().build();
    }

    @Bean
    @DependsOn({"eventDispatcher", "discordEventBeanProcessor"})
    @ConditionalOnMissingBean
    public GatewayDiscordClient gatewayDiscordClient(EventDispatcher eventDispatcher) {
        return this.login(eventDispatcher).block();
    }

    /**
     * Call to {@link GatewayBootstrap#login()} and return the discord client.
     *
     * @return {@link Mono <GatewayDiscordClient>}.
     */
    public Mono<GatewayDiscordClient> login(EventDispatcher eventDispatcher) {
        GatewayBootstrap<GatewayOptions> gateway = this.discordClient.gateway();
        return gateway
                .setEventDispatcher(eventDispatcher)
                .login()
                .doOnNext(this::startAwaitThread);
    }

    private void startAwaitThread(final GatewayDiscordClient gatewayDiscordClient) {
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
