package org.example.controller;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository repository;

  public UserController(UserRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<User> getAllUsers() {
    return repository.findAll();
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return repository.save(user);
  }
}
