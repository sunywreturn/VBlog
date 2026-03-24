package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sang.bean.Article;
import org.sang.bean.RespBean;
import org.sang.bean.User;
import org.sang.service.ArticleService;
import org.sang.utils.Util;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setNickname("测试用户");

        testArticle = new Article();
        testArticle.setId(100L);
        testArticle.setTitle("测试文章标题");
        testArticle.setMdContent("测试Markdown内容");
        testArticle.setHtmlContent("测试HTML内容");
        testArticle.setSummary("测试摘要");
        testArticle.setState(0);
    }

    @Test
    void addNewArticle_shouldReturnSuccessRespBean_whenAddSucceeds() {
        when(articleService.addNewArticle(testArticle)).thenReturn(1);

        RespBean result = articleController.addNewArticle(testArticle);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("100", result.getMsg());
        verify(articleService).addNewArticle(testArticle);
    }

    @Test
    void addNewArticle_shouldReturnErrorRespBeanForSaveFailure_whenStateIs0AndAddFails() {
        testArticle.setState(0);
        when(articleService.addNewArticle(testArticle)).thenReturn(0);

        RespBean result = articleController.addNewArticle(testArticle);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("文章保存失败!", result.getMsg());
        verify(articleService).addNewArticle(testArticle);
    }

    @Test
    void addNewArticle_shouldReturnErrorRespBeanForPublishFailure_whenStateIsNot0AndAddFails() {
        testArticle.setState(1);
        when(articleService.addNewArticle(testArticle)).thenReturn(0);

        RespBean result = articleController.addNewArticle(testArticle);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("文章发表失败!", result.getMsg());
        verify(articleService).addNewArticle(testArticle);
    }

    @Test
    void uploadImg_shouldReturnSuccessRespBean_whenUploadSucceeds() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("/blog");
        when(request.getServletContext()).thenReturn(mock(javax.servlet.ServletContext.class));
        
        String expectedFilePath = "/blogimg/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        String mockRealPath = System.getProperty("java.io.tmpdir") + expectedFilePath;
        when(request.getServletContext().getRealPath(expectedFilePath)).thenReturn(mockRealPath);

        MockMultipartFile image = new MockMultipartFile(
            "image", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        RespBean result = articleController.uploadImg(request, image);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertTrue(result.getMsg().contains("http://localhost:8080/blog/blogimg/"));
        assertTrue(result.getMsg().contains("test.jpg"));
    }

    @Test
    void uploadImg_shouldReturnErrorRespBean_whenIOExceptionOccurs() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("/blog");
        when(request.getServletContext()).thenReturn(mock(javax.servlet.ServletContext.class));
        
        String expectedFilePath = "/blogimg/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        String mockRealPath = System.getProperty("java.io.tmpdir") + expectedFilePath;
        when(request.getServletContext().getRealPath(expectedFilePath)).thenReturn(mockRealPath);

        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("test.jpg");
        when(image.getBytes()).thenThrow(new IOException("Test IO exception"));

        RespBean result = articleController.uploadImg(request, image);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("上传失败!", result.getMsg());
    }

    @Test
    void getArticleByState_shouldReturnArticleMap_whenServiceReturnsData() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        List<Article> articles = Arrays.asList(testArticle);
        when(articleService.getArticleCountByState(-1, 1L, null)).thenReturn(1);
        when(articleService.getArticleByState(-1, 1, 6, null)).thenReturn(articles);

        Map<String, Object> result = articleController.getArticleByState(-1, 1, 6, null);

        assertNotNull(result);
        assertEquals(1, result.get("totalCount"));
        assertEquals(articles, result.get("articles"));
        verify(articleService).getArticleCountByState(-1, 1L, null);
        verify(articleService).getArticleByState(-1, 1, 6, null);
    }

    @Test
    void getArticleByState_shouldReturnEmptyMap_whenServiceReturnsNoData() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        when(articleService.getArticleCountByState(0, 1L, "test")).thenReturn(0);
        when(articleService.getArticleByState(0, 1, 6, "test")).thenReturn(Collections.emptyList());

        Map<String, Object> result = articleController.getArticleByState(0, 1, 6, "test");

        assertNotNull(result);
        assertEquals(0, result.get("totalCount"));
        assertTrue(((List<?>) result.get("articles")).isEmpty());
        verify(articleService).getArticleCountByState(0, 1L, "test");
        verify(articleService).getArticleByState(0, 1, 6, "test");
    }

    @Test
    void getArticleById_shouldReturnArticle_whenServiceReturnsArticle() {
        when(articleService.getArticleById(100L)).thenReturn(testArticle);

        Article result = articleController.getArticleById(100L);

        assertNotNull(result);
        assertEquals(testArticle.getId(), result.getId());
        assertEquals(testArticle.getTitle(), result.getTitle());
        verify(articleService).getArticleById(100L);
    }

    @Test
    void getArticleById_shouldReturnNull_whenServiceReturnsNull() {
        when(articleService.getArticleById(999L)).thenReturn(null);

        Article result = articleController.getArticleById(999L);

        assertNull(result);
        verify(articleService).getArticleById(999L);
    }

    @Test
    void updateArticleState_shouldReturnSuccessRespBean_whenAllArticlesUpdated() {
        Long[] aids = {100L, 101L, 102L};
        when(articleService.updateArticleState(aids, 2)).thenReturn(3);

        RespBean result = articleController.updateArticleState(aids, 2);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
        verify(articleService).updateArticleState(aids, 2);
    }

    @Test
    void updateArticleState_shouldReturnErrorRespBean_whenNotAllArticlesUpdated() {
        Long[] aids = {100L, 101L, 102L};
        when(articleService.updateArticleState(aids, 2)).thenReturn(2);

        RespBean result = articleController.updateArticleState(aids, 2);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(articleService).updateArticleState(aids, 2);
    }

    @Test
    void updateArticleState_shouldReturnErrorRespBean_whenNoArticlesUpdated() {
        Long[] aids = {100L, 101L, 102L};
        when(articleService.updateArticleState(aids, 2)).thenReturn(0);

        RespBean result = articleController.updateArticleState(aids, 2);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(articleService).updateArticleState(aids, 2);
    }

    @Test
    void restoreArticle_shouldReturnSuccessRespBean_whenRestoreSucceeds() {
        when(articleService.restoreArticle(100)).thenReturn(1);

        RespBean result = articleController.restoreArticle(100);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("还原成功!", result.getMsg());
        verify(articleService).restoreArticle(100);
    }

    @Test
    void restoreArticle_shouldReturnErrorRespBean_whenRestoreFails() {
        when(articleService.restoreArticle(100)).thenReturn(0);

        RespBean result = articleController.restoreArticle(100);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("还原失败!", result.getMsg());
        verify(articleService).restoreArticle(100);
    }

    @Test
    void dataStatistics_shouldReturnStatisticsMap_whenServiceReturnsData() {
        List<String> categories = Arrays.asList("技术", "生活", "随笔");
        List<Integer> dataStats = Arrays.asList(10, 5, 3);
        
        when(articleService.getCategories()).thenReturn(categories);
        when(articleService.getDataStatistics()).thenReturn(dataStats);

        Map<String, Object> result = articleController.dataStatistics();

        assertNotNull(result);
        assertEquals(categories, result.get("categories"));
        assertEquals(dataStats, result.get("ds"));
        verify(articleService).getCategories();
        verify(articleService).getDataStatistics();
    }

    @Test
    void dataStatistics_shouldReturnEmptyMap_whenServiceReturnsEmptyData() {
        when(articleService.getCategories()).thenReturn(Collections.emptyList());
        when(articleService.getDataStatistics()).thenReturn(Collections.emptyList());

        Map<String, Object> result = articleController.dataStatistics();

        assertNotNull(result);
        assertTrue(((List<?>) result.get("categories")).isEmpty());
        assertTrue(((List<?>) result.get("ds")).isEmpty());
        verify(articleService).getCategories();
        verify(articleService).getDataStatistics();
    }
}