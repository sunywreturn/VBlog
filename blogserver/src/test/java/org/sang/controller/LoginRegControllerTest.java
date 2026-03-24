package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sang.bean.RespBean;
import org.sang.bean.User;
import org.sang.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginRegControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginRegController loginRegController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
    }

    @Test
    void loginError_shouldReturnErrorRespBean() {
        RespBean result = loginRegController.loginError();

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("登录失败!", result.getMsg());
    }

    @Test
    void loginSuccess_shouldReturnSuccessRespBean() {
        RespBean result = loginRegController.loginSuccess();

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("登录成功!", result.getMsg());
    }

    @Test
    void loginPage_shouldReturnErrorRespBean_whenNotLoggedIn() {
        RespBean result = loginRegController.loginPage();

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("尚未登录，请登录!", result.getMsg());
    }

    @Test
    void reg_shouldReturnSuccess_whenRegistrationSucceeds() {
        when(userService.reg(testUser)).thenReturn(0);

        RespBean result = loginRegController.reg(testUser);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("注册成功!", result.getMsg());
        verify(userService).reg(testUser);
    }

    @Test
    void reg_shouldReturnError_whenUsernameIsDuplicate() {
        when(userService.reg(testUser)).thenReturn(1);

        RespBean result = loginRegController.reg(testUser);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("用户名重复，注册失败!", result.getMsg());
        verify(userService).reg(testUser);
    }

    @Test
    void reg_shouldReturnError_whenRegistrationFails() {
        when(userService.reg(testUser)).thenReturn(2);

        RespBean result = loginRegController.reg(testUser);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("注册失败!", result.getMsg());
        verify(userService).reg(testUser);
    }

    @Test
    void reg_shouldReturnError_whenServiceReturnsNegativeValue() {
        when(userService.reg(testUser)).thenReturn(-1);

        RespBean result = loginRegController.reg(testUser);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("注册失败!", result.getMsg());
        verify(userService).reg(testUser);
    }
}
