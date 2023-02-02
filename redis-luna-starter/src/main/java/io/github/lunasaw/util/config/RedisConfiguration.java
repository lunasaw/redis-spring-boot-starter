package io.github.lunasaw.util.config;

import io.github.lunasaw.util.*;
import io.github.lunasaw.util.cache.LocalCacheUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author: luna
 */
@Configuration
public class RedisConfiguration {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheUtil localCacheUtil() {
        return new LocalCacheUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisZSetUtil redisZSetUtil() {
        return new RedisZSetUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisKeyUtil redisKeyUtil() {
        return new RedisKeyUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisListUtil redisListUtil() {
        return new RedisListUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisHashUtil redisHashUtil() {
        return new RedisHashUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisSetUtil redisSetUtil() {
        return new RedisSetUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisValueUtil redisValueUtil() {
        return new RedisValueUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisGeoUtil redisGeoUtil() {
        return new RedisGeoUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisStreamUtil redisStreamUtil() {
        return new RedisStreamUtil();
    }
}
