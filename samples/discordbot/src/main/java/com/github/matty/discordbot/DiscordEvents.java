package com.github.matty.discordbot;

import com.github.matty.discord4j.spring.annotations.DiscordErrorHandler;
import com.github.matty.discord4j.spring.annotations.DiscordEventListener;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DiscordEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEvents.class);

    private List<MessageHandler> messageHandlers;

    @Autowired
    public DiscordEvents(List<MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    @DiscordEventListener
    public void handleConnect(ConnectEvent connectEvent) {
        LOGGER.info("Connected to Discord.");
    }

    @DiscordEventListener
    public Mono<Message> messageReceived(MessageCreateEvent messageCreateEvent) {
        for (MessageHandler handler : messageHandlers) {
            Mono<Message> r = handler.messageReceive(messageCreateEvent);
            if (r != null) {
                return r;
            }
        }

        return Mono.empty();
    }

    @DiscordErrorHandler
    public void onError(Throwable e) {
        System.out.println("Error!");
    }
}
