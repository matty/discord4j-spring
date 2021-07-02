package com.github.matty.discordbot;

import com.github.matty.discord4j.spring.annotations.EnableDiscord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscord
public class SpringDiscordApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringDiscordApp.class);
    }
}
