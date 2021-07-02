package com.github.matty.discordbot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EchoCommandHandler implements MessageHandler {
    @Override
    public Mono<Message> messageReceive(MessageCreateEvent messageCreateEvent) {
        Message msg = messageCreateEvent.getMessage();

        if ("!echo".equals(msg.getContent())) {
            return msg.getChannel()
                    .flatMap(ch -> ch.createMessage("Echo"));
        }

        return null;
    }
}
