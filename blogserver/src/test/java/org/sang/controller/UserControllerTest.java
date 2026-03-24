package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sang.bean.RespBean;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        
        // 设置角色和权限
        List<Role> roles = new ArrayList<>();
        Role adminRole = new Role();
        adminRole.setName("超级管理员");
        roles.add(adminRole);
        testUser.setRoles(roles);
    }

    @Test
    void currentUserName_shouldReturnCurrentUserNickname() {
        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        String result = userController.currentUserName();

        assertEquals("测试用户", result);
    }

    @Test
    void currentUserId_shouldReturnCurrentUserId() {
        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        Long result = userController.currentUserId();

        assertEquals(1L, result);
    }

    @Test
    void currentUserEmail_shouldReturnCurrentUserEmail() {
        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        String result = userController.currentUserEmail();

        assertEquals("test@example.com", result);
    }

    @Test
    void isAdmin_shouldReturnTrue_whenUserHasAdminRole() {
        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        Boolean result = userController.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserDoesNotHaveAdminRole() {
        // 创建没有管理员角色的用户
        User nonAdminUser = new User();
        nonAdminUser.setId(2L);
        List<Role> roles = new ArrayList<>();
        Role userRole = new Role();
        userRole.setName("普通用户");
        roles.add(userRole);
        nonAdminUser.setRoles(roles);

        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        SecurityContextHolder.setContext(securityContext);

        Boolean result = userController.isAdmin();

        assertFalse(result);
    }

    @Test
    void isAdmin_shouldReturnFalse_whenUserHasNoRoles() {
        // 创建没有角色的用户
        User noRoleUser = new User();
        noRoleUser.setId(3L);
        noRoleUser.setRoles(new ArrayList<>());

        // 模拟SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(noRoleUser);
        SecurityContextHolder.setContext(securityContext);

        Boolean result = userController.isAdmin();

        assertFalse(result);
    }

    @Test
    void updateUserEmail_shouldReturnSuccessRespBean_whenUpdateSucceeds() {
        String email = "newemail@example.com";
        when(userService.updateUserEmail(email)).thenReturn(1);

        RespBean result = userController.updateUserEmail(email);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("开启成功!", result.getMsg());
        verify(userService).updateUserEmail(email);
    }

    @Test
    void updateUserEmail_shouldReturnErrorRespBean_whenUpdateFails() {
        String email = "newemail@example.com";
        when(userService.updateUserEmail(email)).thenReturn(0);

        RespBean result = userController.updateUserEmail(email);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("开启失败!", result.getMsg());
        verify(userService).updateUserEmail(email);
    }

    @Test
    void updateUserEmail_shouldReturnErrorRespBean_whenServiceReturnsNegativeValue() {
        String email = "newemail@example.com";
        when(userService.updateUserEmail(email)).thenReturn(-1);

        RespBean result = userController.updateUserEmail(email);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("开启失败!", result.getMsg());
        verify(userService).updateUserEmail(email);
    }
}