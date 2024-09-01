package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.UserDetailsDto;
import com.wyaaung.rbac.dto.UserDto;
import com.wyaaung.rbac.service.UserService;
import com.wyaaung.rbac.transformer.UserTransformer;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<UserDto> getUsers() {
    return userService.getUsers().stream().map(UserTransformer::toDto).toList();
  }

  @GetMapping("/{username}")
  @ResponseStatus(OK)
  public UserDetailsDto getUser(@PathVariable("username") final String username) {
    return UserTransformer.toUserDetailsDto(userService.getUser(username));
  }
}
