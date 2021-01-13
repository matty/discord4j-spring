# Discord4J Spring
Integrate Discord4J into a Spring Boot application.

**This project is work in progress.**

# Usage
To get started add this library to your Spring Boot project and enable the Discord integration.

```java
@SpringBootApplication
@EnableDiscord
public class DiscordApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscordApplication.class);
    }
}
```

# Events
You can listen to any event using the DiscordEventListener annotation.

```java
@Component
public class PingMessageEvent {
    @DiscordEventListener
    public Mono<Message> pingCommand(MessageCreateEvent messageCreateEvent) {
        Message msg = messageCreateEvent.getMessage();
        if ("!ping".equals(msg.getContent())) {
            return msg.getChannel()
                    .flatMap(ch -> ch.createMessage("Pong!"));
        }

        return Mono.empty();
    }
}
```

# Token

To provide a Discord token you can add it to your configuration file ``discord.token=`` or as a startup parameter ``-Ddiscord.token=""``. If you wish you can declare a custom token provider by creating a configuration class with a ``DiscordTokenProvider`` bean.

```java
@Configuration
public class DiscordConfiguration {
    @Bean
    public DiscordTokenProvider tokenProvider() {
        return () -> "my_token";
    }
}
```

