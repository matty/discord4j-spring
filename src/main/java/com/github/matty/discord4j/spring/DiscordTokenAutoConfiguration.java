package com.github.matty.discord4j.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordTokenAutoConfiguration {

    @Bean("discordTokenProvider")
    @ConditionalOnMissingBean
    public DiscordTokenProvider tokenProvider(@Value("${discord.token}") String token) {
        return () -> token;
    }
}
