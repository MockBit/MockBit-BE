package com.example.mockbit.user.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.user.application.response.UserAppResponse;
import com.example.mockbit.user.application.UserService;
import com.example.mockbit.user.application.request.UserJoinAppRequest;
import com.example.mockbit.user.application.request.UserUpdateAppRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Long> createUser(
            @Valid @RequestBody UserJoinAppRequest request
    ) {
        Long id = userService.join(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAppResponse> getUser(@PathVariable Long id) {
        UserAppResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(
            @Login Long id,
            @Valid @RequestBody UserUpdateAppRequest request
    ) {
        userService.updateUser(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
