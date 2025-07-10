package com.example.demoSpringWithBuck4J.controller.config;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withSsl(false)
                .build());
    }

    @Bean
    public ProxyManager<String> lettuceBasedProxyManager() {
        RedisClient redisClient = redisClient();
        StatefulRedisConnection<String, byte[]> redisConnection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(redisConnection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1L)))
                .build();
    }

    //    @Bean
//    public Supplier<BucketConfiguration> bucketConfiguration() {
//        return () -> BucketConfiguration.builder()
//                .addLimit(Bandwidth.simple(200L, Duration.ofMinutes(1L)))
//                .build();
//    }
    @Bean
    public Supplier<BucketConfiguration> bucketConfiguration() {
        return () -> {
            String capacityStr = redisTemplate.opsForValue().get("rate-limit:capacity");
            long capacity = capacityStr != null ? Long.parseLong(capacityStr) : 10L;

            log.info("Dynamic capacity: {}", capacity);

            return BucketConfiguration.builder()
                    .addLimit(Bandwidth.simple(capacity, Duration.ofMinutes(5)))
                    .build();
        };
    }


}