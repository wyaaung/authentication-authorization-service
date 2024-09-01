package com.wyaaung.rbac.configuration.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class LoggingFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
      chain.doFilter(request, response);
      return;
    }

    try {
      MDC.put("user", SecurityContextHolder.getContext().getAuthentication().getName());
      MDC.put("path", getCurrentUrlFromRequest((HttpServletRequest) request));
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  private String getCurrentUrlFromRequest(final HttpServletRequest request) {
    final String requestURL = request.getRequestURI();
    final String queryString = request.getQueryString();

    return queryString == null ? requestURL : String.format("%s?%s", requestURL, queryString);
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
  }
}
