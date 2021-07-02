package com.github.matty.discordbot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface MessageHandler {

    Mono<Message> messageReceive(MessageCreateEvent messageCreateEvent);
}
