package com.github.matty.discord4j.spring.annotations;

import com.github.matty.discord4j.spring.DiscordAutoConfiguration;
import com.github.matty.discord4j.spring.beans.DiscordEventBeanProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable Discord support in a Spring Boot application.
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({DiscordAutoConfiguration.class, DiscordEventBeanProcessor.class})
public @interface EnableDiscord {
}
