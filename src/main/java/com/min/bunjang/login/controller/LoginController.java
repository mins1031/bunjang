package com.min.bunjang.login.controller;

import com.min.bunjang.common.dto.RestResponse;
import com.min.bunjang.login.dto.LoginRequest;
import com.min.bunjang.login.dto.LoginResponse;
import com.min.bunjang.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping(LoginControllerPath.LOGIN)
    public RestResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = loginService.login(loginRequest);
        return RestResponse.of(HttpStatus.OK, loginResponse);
    }
}
