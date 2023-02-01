package io.github.lunasaw.util.cache;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import io.github.lunasaw.util.RedisHashUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author chenzhangyue
 * 2023/2/1
 */
@Component
@Data
public class LocalCacheUtil {

    @Autowired
    private RedisHashUtil redisHashUtil;

    private String localKey;

    LoadingCache<String, Object> masterLevelSettingCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .expireAfterWrite(2L, TimeUnit.MINUTES)

            .build(new CacheLoader<String, Object>() {
                public Object load(String key) {
                    List<String> list = Splitter.on("_").splitToList(key);

                    return redisHashUtil.multiGet(localKey, Sets.newHashSet(list));
                }
            });


}
