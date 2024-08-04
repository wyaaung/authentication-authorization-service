package com.wyaaung.rbac.dto;

import java.time.Instant;

public record ExceptionDto(Long timestamp, Integer status, String error, String message) {
  public ExceptionDto(Integer status, String error, String message) {
    this(Instant.now().toEpochMilli(), status, error, message);
  }
}
