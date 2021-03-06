package com.min.bunjang.login.controller;

import com.min.bunjang.config.ControllerBaseTest;
import com.min.bunjang.login.dto.LoginRequest;
import com.min.bunjang.login.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
class LoginControllerTest extends ControllerBaseTest {
    @MockBean
    private LoginService loginService;


    @DisplayName("로그인 컨트롤러가 200을 응답한다")
    @Test
    void name() throws Exception {
        //given
        String email = "email";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);

        //when & then
        mockMvc.perform(post(LoginControllerPath.LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("loginRequest의 필드 누락 요청시 400리턴")
    @ParameterizedTest
    @CsvSource(value = {"'', password", "' ',' '"})
    void name2(String email, String password) throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(email, password);

        //when & then
        mockMvc.perform(post(LoginControllerPath.LOGIN)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}