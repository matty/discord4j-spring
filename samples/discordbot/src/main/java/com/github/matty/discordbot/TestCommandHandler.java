package com.github.matty.discordbot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TestCommandHandler implements MessageHandler {
    @Override
    public Mono<Message> messageReceive(MessageCreateEvent messageCreateEvent) {
        Message msg = messageCreateEvent.getMessage();

        if ("!test".equals(msg.getContent())) {
            return msg.getChannel()
                    .flatMap(ch -> ch.createMessage("Hello"));
        }

        return null;
    }
}
