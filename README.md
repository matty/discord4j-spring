# Discord4J Spring
Integrate Discord4J into a Spring Boot application.

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
    @DiscordEventListener(MessageCreateEvent.class)
    public void handleBotStatusCommand(Mono<MessageCreateEvent> messageCreateEvent) {
        messageCreateEvent.map(MessageCreateEvent::getMessage)
                .filter(m -> "!ping".equals(m.getContent()))
                .flatMap(Message::getChannel)
                .flatMap(ch -> ch.createMessage("Pong!"))
                .subscribe();
    }
}
```

# Token

To provide a Discord you can add it to your configuration file ``discord.token=`` or as a startup parameter ``-Ddiscord.token=""``. If you wish you can declare a custom token provider by creating a configuration class with a ``DiscordTokenProvider`` bean.

```java
@Configuration
public class DiscordConfiguration {
    @Bean
    public DiscordTokenProvider tokenProvider() {
        return () -> "my_token";
    }
}
```

