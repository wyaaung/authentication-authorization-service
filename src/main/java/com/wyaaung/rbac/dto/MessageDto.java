package com.wyaaung.rbac.dto;

import java.time.Instant;

public record MessageDto(Long timestamp, String message) {
  public MessageDto(String message) {
    this(Instant.now().toEpochMilli(), message);
  }
}
