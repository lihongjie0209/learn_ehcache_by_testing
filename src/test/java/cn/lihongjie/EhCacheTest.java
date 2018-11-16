package cn.lihongjie;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.hamcrest.core.Is;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class EhCacheTest {


    private static int cacheCount;
    Logger logger = LoggerFactory.getLogger(EhCacheTest.class);


    private static String cacheName = "testCache";
    private static CacheManager cacheManager;
    private Cache<String, String> testCache;

    @BeforeClass
    public static void init() {


        cacheCount = 10;
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(cacheName,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                String.class,
                                ResourcePoolsBuilder.heap(cacheCount)
                        )

                        )

                .build();
        cacheManager.init();






    }


    @AfterClass
    public static void destroy(){


        cacheManager.removeCache(EhCacheTest.cacheName);

        cacheManager.close();

    }


    @Before
    public void setUp() throws Exception {


        testCache = cacheManager.getCache(EhCacheTest.cacheName, String.class, String.class);

        testCache.clear();
    }

    @Test
    public void testPutToCacheAndGetFromCache() {

        testCache.put("k1", "v1");
        assertThat(testCache.get("k1"), is("v1"));


    }


    @Test
    public void testInsertTooManyElementInCache() {


        IntStream.rangeClosed(1, 10000)
            .forEachOrdered(i -> testCache.put(String.valueOf(i), UUID.randomUUID().toString()));

        ;


        // 默认使用LRU缓存
        for (Cache.Entry<String, String> entry : testCache) {

            logger.info(String.format("%s %s", entry.getKey(), entry.getValue()));
        }


        long count = StreamSupport.stream(testCache.spliterator(), false).count();

        assertThat(((int) count), is(cacheCount));

    }



}
