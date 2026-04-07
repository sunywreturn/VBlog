package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sang.bean.RespBean;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        userController.userService = userService;
    }

    private void setMockCurrentUser(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void currentUserName_shouldReturnCurrentUserNickname() {
        User user = new User();
        user.setNickname("testUser");
        setMockCurrentUser(user);

        String result = userController.currentUserName();

        assertEquals("testUser", result);
    }

    @Test
    void currentUserName_shouldReturnEmptyStringForEmptyNickname() {
        User user = new User();
        user.setNickname("");
        setMockCurrentUser(user);

        String result = userController.currentUserName();

        assertEquals("", result);
    }

    @Test
    void currentUserName_shouldReturnNullForNullNickname() {
        User user = new User();
        setMockCurrentUser(user);

        String result = userController.currentUserName();

        assertNull(result);
    }

    @Test
    void currentUserId_shouldReturnCurrentUserId() {
        User user = new User();
        user.setId(1L);
        setMockCurrentUser(user);

        Long result = userController.currentUserId();

        assertEquals(1L, result);
    }

    @Test
    void currentUserId_shouldReturnNullForNullId() {
        User user = new User();
        setMockCurrentUser(user);

        Long result = userController.currentUserId();

        assertNull(result);
    }

    @Test
    void currentUserEmail_shouldReturnCurrentUserEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        setMockCurrentUser(user);

        String result = userController.currentUserEmail();

        assertEquals("test@example.com", result);
    }

    @Test
    void currentUserEmail_shouldReturnEmptyStringForEmptyEmail() {
        User user = new User();
        user.setEmail("");
        setMockCurrentUser(user);

        String result = userController.currentUserEmail();

        assertEquals("", result);
    }

    @Test
    void currentUserEmail_shouldReturnNullForNullEmail() {
        User user = new User();
        setMockCurrentUser(user);

        String result = userController.currentUserEmail();

        assertNull(result);
    }

    @Test
    void isAdmin_shouldReturnTrueWhenUserHasSuperAdminRole() {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "超级管理员"));
        user.setRoles(roles);
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_shouldReturnTrueWhenUserHasMultipleRolesWithSuperAdmin() {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "普通用户"));
        roles.add(new Role(2L, "超级管理员"));
        roles.add(new Role(3L, "编辑"));
        user.setRoles(roles);
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_shouldReturnTrueWhenRoleNameContainsSuperAdmin() {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "系统超级管理员"));
        user.setRoles(roles);
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertTrue(result);
    }

    @Test
    void isAdmin_shouldReturnFalseWhenUserHasNoRoles() {
        User user = new User();
        user.setRoles(new ArrayList<>());
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertFalse(result);
    }

    @Test
    void isAdmin_shouldReturnFalseWhenUserHasNoSuperAdminRole() {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1L, "普通用户"));
        roles.add(new Role(2L, "编辑"));
        user.setRoles(roles);
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertFalse(result);
    }

    @Test
    void isAdmin_shouldReturnFalseWhenUserHasNullRoles() {
        User user = new User();
        setMockCurrentUser(user);

        Boolean result = userController.isAdmin();

        assertFalse(result);
    }

    @Test
    void updateUserEmail_shouldReturnSuccessRespBeanWhenUpdateSucceeds() {
        when(userService.updateUserEmail("newemail@example.com")).thenReturn(1);

        RespBean result = userController.updateUserEmail("newemail@example.com");

        assertEquals("success", result.getStatus());
        assertEquals("开启成功!", result.getMsg());
    }

    @Test
    void updateUserEmail_shouldReturnErrorRespBeanWhenUpdateFails() {
        when(userService.updateUserEmail("invalid@example.com")).thenReturn(0);

        RespBean result = userController.updateUserEmail("invalid@example.com");

        assertEquals("error", result.getStatus());
        assertEquals("开启失败!", result.getMsg());
    }

    @Test
    void updateUserEmail_shouldHandleEmptyEmail() {
        when(userService.updateUserEmail("")).thenReturn(0);

        RespBean result = userController.updateUserEmail("");

        assertEquals("error", result.getStatus());
        assertEquals("开启失败!", result.getMsg());
    }

    @Test
    void updateUserEmail_shouldHandleNullEmail() {
        when(userService.updateUserEmail(null)).thenReturn(0);

        RespBean result = userController.updateUserEmail(null);

        assertEquals("error", result.getStatus());
        assertEquals("开启失败!", result.getMsg());
    }
}
