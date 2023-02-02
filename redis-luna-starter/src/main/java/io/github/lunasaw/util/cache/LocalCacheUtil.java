package io.github.lunasaw.util.cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.lunasaw.util.RedisValueUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Component
@Data
public class LocalCacheUtil<K, T> {

    @Autowired
    private RedisValueUtil redisValueUtil;

    private String localKey;

    LoadingCache<String, Object> loadingCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .expireAfterWrite(2L, TimeUnit.MINUTES)

            .build(new CacheLoader<String, Object>() {
                public Object load(String key) {
                    return redisValueUtil.get(key);
                }
            });

    @SneakyThrows
    public Object get(String key) {
        return loadingCache.get(key);
    }

    public void set(String key, Object value) {
        loadingCache.put(key, value);
    }
}
