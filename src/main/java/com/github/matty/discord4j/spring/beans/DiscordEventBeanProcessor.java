package com.github.matty.discord4j.spring.beans;

import com.github.matty.discord4j.spring.annotations.DiscordEventListener;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BeanPostProcessor for {@link Event}'s.
 *
 * @author Matty Southall
 * @since 1.0
 */
@Component("discordEventBeanProcessor")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordEventBeanProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEventBeanProcessor.class);

    EventDispatcher eventDispatcher;

    @Autowired
    public DiscordEventBeanProcessor(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            if (method.isAnnotationPresent(DiscordEventListener.class)) {
                doDiscordEventListener(method, bean);
            }
        });

        return bean;
    }

    private void doDiscordEventListener(Method method, Object bean) {
        DiscordEventListener discordEventListener
                = method.getAnnotation(DiscordEventListener.class);

        LOGGER.debug("Register {} for event {}.", bean.getClass().getSimpleName(),
                discordEventListener.value().getSimpleName());

        eventDispatcher.on(discordEventListener.value())
                .doOnNext(event -> onEvent(Mono.just(event), method, bean)).subscribe();
    }

    private void onEvent(Mono<? extends Event> event, Method method, Object bean) {
        try {
            method.invoke(bean, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
