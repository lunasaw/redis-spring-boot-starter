package io.github.lunasaw.util;

import com.alibaba.fastjson.TypeReference;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.lunasaw.util.inter.Namespace;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 解决缓存穿透
 */
public class CacheQueryUtils {
    @Setter
    @Getter
    public static class Req<K, T> {

        private List<K> keys;
        private Namespace namespace;
        private int expiredTime;
        private SQL<K, T> sql;
        private TypeRef typeReference;
        private Validate<T> validate;
        private KeyGenerate<K, T> keyGenerate;
        private Mock<T> mock;
    }

    public interface TypeRef {
        TypeReference run();
    }

    public interface SQL<K, T> {
        List<T> run(List<K> keys);
    }

    public interface Validate<T> {
        boolean run(T t);
    }

    public interface KeyGenerate<K, T> {
        K run(T t);
    }

    public interface Mock<T> {
        T run();
    }

    /**
     * 包装MGET,且解决缓存穿透问题
     *
     * @param redisHashUtil 操作key的bean
     * @param req           入参
     * @param useCache      是否使用缓存
     * @param <K>
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> batchQuery(RedisHashUtil redisHashUtil, Req<K, T> req, boolean useCache) {

        Map<K, T> rMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(req.keys)) {
            return rMap;
        }

        boolean rwCache = (useCache && req.expiredTime > 0);
        //去重
        HashSet<K> keySet = Sets.newHashSet(req.keys);
        List<K> noCacheIds = Lists.newArrayList(keySet);
        /*--------------------- 走缓存 ----------------------*/
        if (rwCache) {
            List<T> list = redisHashUtil.multiGet(req.namespace.getNamespace(), keySet, req.typeReference.run());
            Map<K, T> cacheSettings = list2Map(list, req.keyGenerate);

            if (MapUtils.isNotEmpty(cacheSettings)) {
                for (Map.Entry<K, T> ktEntry : cacheSettings.entrySet()) {
                    //这里过滤掉mock数据
                    if (req.validate.run(ktEntry.getValue())) {
                        rMap.put(ktEntry.getKey(), ktEntry.getValue());
                    }
                }
                noCacheIds.removeAll(cacheSettings.keySet());
            }
        }

        /*--------------------- db补数据 ---------------------*/
        if (CollectionUtils.isNotEmpty(noCacheIds)) {
            List<T> settingList = req.sql.run(noCacheIds);
            Map<K, T> dbDOMap = list2Map(settingList, req.keyGenerate);
            if (CollectionUtils.isNotEmpty(settingList)) {
                rMap.putAll(dbDOMap);
            }

            if (rwCache) {
                //缓存数据填充
                Map<K, T> cacheMap = Maps.newHashMap();
                for (K key : noCacheIds) {
                    T t = dbDOMap.get(key);
                    if (t == null) {
                        //mock数据
                        t = req.mock.run();
                    }
                    cacheMap.put(key, t);
                }

                redisHashUtil.set(req.namespace.getNamespace(), cacheMap, req.expiredTime);
            }
        }

        return rMap;
    }

    public static <K, T> Map<K, T> list2Map(List<T> dbList, KeyGenerate<K, T> keyGenerate) {

        Map<K, T> map = new HashMap();
        for (T infoDO : dbList) {
            if (infoDO != null) {
                map.put(keyGenerate.run(infoDO), infoDO);
            }
        }
        return map;
    }

}
