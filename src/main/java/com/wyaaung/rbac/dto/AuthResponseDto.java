package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseDto(
  @JsonProperty("token") TokenDto tokenDto,
  @JsonProperty("user") UserDetailsDto userDetailsDto
) {
}
