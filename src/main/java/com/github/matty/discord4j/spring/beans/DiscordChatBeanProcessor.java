package com.github.matty.discord4j.spring.beans;

import com.github.matty.discord4j.spring.annotations.DiscordChat;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Pattern;

@Component("discordChatBeanProcessor")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordChatBeanProcessor implements BeanPostProcessor {
    private static final String REG_EXP_START = "$regexp:";

    EventDispatcher eventDispatcher;

    @Autowired
    public DiscordChatBeanProcessor( EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), method -> {
            if (method.isAnnotationPresent(DiscordChat.class)) {
                doDiscordChat(method, bean);
            }
        });

        return bean;
    }

    /**
     * Methods annotated with {@link DiscordChat}.
     */
    @SuppressWarnings("unchecked")
    private void doDiscordChat(Method method, Object bean) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            if (MessageCreateEvent.class.isAssignableFrom(parameterTypes[0])) {
                doChatEvent(method, bean, (Class<MessageCreateEvent>) parameterTypes[0]);
            }
        }
    }

    private void doChatEvent(Method method, Object bean, Class<MessageCreateEvent> parameterClazz) {
        DiscordChat discordChat = method.getAnnotation(DiscordChat.class);
        eventDispatcher.on(parameterClazz)
                .filter(msg -> applyFilter(msg, discordChat.filter()))
                .flatMap(e -> {
                    Optional<Object> r = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean, e));
                    return r.filter(o -> o instanceof Publisher)
                            .map(o -> (Publisher)o)
                            .orElse(Mono.empty());
                })
                .doOnError(error -> EventUtils.doOnError(bean, error))
                .onErrorStop()
                .subscribe();
    }

    private boolean applyFilter(MessageCreateEvent messageCreateEvent, String filter) {
        Message msg = messageCreateEvent.getMessage();
        if (filter.startsWith(REG_EXP_START)) {
            return Pattern.compile(filter.split(":")[1])
                    .matcher(msg.getContent())
                    .find();
        } else {
            return msg.getContent().equals(filter);
        }
    }
}
