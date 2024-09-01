package com.wyaaung.rbac.configuration;

import ch.qos.logback.access.jetty.RequestLogImpl;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessLogsConfiguration {
  @Bean
  public WebServerFactoryCustomizer<JettyServletWebServerFactory> accessLogsCustomizer() {
    return factory -> {
      factory.addServerCustomizers(server -> {
        RequestLogImpl requestLog = new RequestLogImpl();
        requestLog.setResource("/logback-access-spring.xml");
        requestLog.setQuiet(false);
        requestLog.start();
        server.setRequestLog(requestLog);
      });
    };
  }
}
