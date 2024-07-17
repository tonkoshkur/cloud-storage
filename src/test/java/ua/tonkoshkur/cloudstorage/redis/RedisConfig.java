package ua.tonkoshkur.cloudstorage.redis;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Configuration
public class RedisConfig {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:latest");

    static {
        int port = 6379;
        redis.withExposedPorts(port)
                .start();
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", redis.getMappedPort(port).toString());
    }
}
