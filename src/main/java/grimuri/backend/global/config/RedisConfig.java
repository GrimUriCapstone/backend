package grimuri.backend.global.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import lombok.RequiredArgsConstructor;
import org.redisson.config.Config;

import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.cache.CacheManager;
import javax.cache.Caching;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final Environment env;

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer().setAddress(env.getProperty("REDIS_ADDRESS"));

        return config;
    }

    @Bean(name = "SpringCM")
    public CacheManager cacheManager(Config config) {
        CacheManager manager = Caching.getCachingProvider().getCacheManager();
        manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        manager.createCache("userList", RedissonConfiguration.fromConfig(config));

        return manager;
    }

    @Bean
    ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }
}
