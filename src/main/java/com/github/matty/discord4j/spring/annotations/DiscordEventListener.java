package com.github.matty.discord4j.spring.annotations;

import discord4j.core.event.domain.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods which Discord4j events are dispatched to.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DiscordEventListener {

    Class<? extends Event> value();
}
