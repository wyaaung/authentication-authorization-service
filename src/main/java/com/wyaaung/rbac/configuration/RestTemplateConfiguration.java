package com.wyaaung.rbac.configuration;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

  private final Integer defaultConcurrency;
  private final Integer defaultMaxPoolSize;
  private final Integer defaultConnectTimeout;
  private final Integer defaultReadTimeout;

  public RestTemplateConfiguration(
    @Value("${rest-template.default.concurrency}") final int defaultConcurrency,
    @Value("${rest-template.default.max-pool-size}") final int defaultMaxPoolSize,
    @Value("${rest-template.default.timeouts.connect}") final int defaultConnectTimeout,
    @Value("${rest-template.default.timeouts.read}") final int defaultReadTimeout) {

    this.defaultConcurrency = defaultConcurrency;
    this.defaultMaxPoolSize = defaultMaxPoolSize;
    this.defaultConnectTimeout = defaultConnectTimeout;
    this.defaultReadTimeout = defaultReadTimeout;
  }

  @Bean
  public RestTemplate defaultRestTemplate() {
    return new RestTemplateBuilder()
      .additionalCustomizers(
        restTemplate ->
          restTemplate.setRequestFactory(
            clientHttpRequestFactory(
              defaultConcurrency,
              defaultMaxPoolSize,
              defaultConnectTimeout,
              defaultReadTimeout)))
      .build();
  }

  private ClientHttpRequestFactory clientHttpRequestFactory(
    final int concurrentRequests,
    final int maxPoolSize,
    final int connectionTimeout,
    final int readTimeout) {
    final PoolingHttpClientConnectionManager connectionManager =
      PoolingHttpClientConnectionManagerBuilder.create()
        .setDefaultConnectionConfig(
          ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
            .setTimeToLive(Timeout.ofMilliseconds(readTimeout))
            .build())
        .setMaxConnPerRoute(concurrentRequests)
        .setMaxConnTotal(maxPoolSize)
        .build();
    final CloseableHttpClient httpClient =
      HttpClients.custom().setConnectionManager(connectionManager).build();
    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }
}
