package com.github.matty.discord4j.spring.beans;

import com.github.matty.discord4j.spring.annotations.DiscordChat;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
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

@Component("discordChatBeanProcessor")
@ConditionalOnBean(GatewayDiscordClient.class)
public class DiscordChatBeanProcessor implements BeanPostProcessor {

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
                .filter(m -> m.getMessage().getContent().equals(discordChat.filter()))
                .flatMap(e -> {
                    Optional<Object> r = Optional.ofNullable(ReflectionUtils.invokeMethod(method, bean, e));
                    return r.filter(o -> o instanceof Publisher)
                            .map(o -> (Publisher)o)
                            .orElse(Mono.empty());
                })
                .subscribe();
    }
}
