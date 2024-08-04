package com.wyaaung.rbac.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

  @Bean
  public CacheManager cacheManager() {
    final SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(List.of(accessControlCache()));
    return cacheManager;
  }

  private CaffeineCache accessControlCache() {
    return new CaffeineCache(
      "accessControlCache", Caffeine.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build());
  }
}
