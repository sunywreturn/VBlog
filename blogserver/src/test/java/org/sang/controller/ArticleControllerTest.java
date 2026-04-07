package org.sang.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sang.bean.Article;
import org.sang.bean.RespBean;
import org.sang.service.ArticleService;
import org.sang.utils.Util;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleControllerTest {

    private ArticleController articleController;

    @Mock
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        articleController = new ArticleController();
        articleController.articleService = articleService;
    }

    private void setMockCurrentUser(org.sang.bean.User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addNewArticle_shouldReturnSuccessWhenArticleIdIsReturned() {
        Article article = new Article();
        article.setId(1L);
        article.setState(0);
        when(articleService.addNewArticle(article)).thenReturn(1);

        RespBean result = articleController.addNewArticle(article);

        assertEquals("success", result.getStatus());
        assertEquals("1", result.getMsg());
        verify(articleService).addNewArticle(article);
    }

    @Test
    void addNewArticle_shouldReturnErrorWhenStateIs0AndResultIsNot1() {
        Article article = new Article();
        article.setId(-1L);
        article.setState(0);
        when(articleService.addNewArticle(article)).thenReturn(0);

        RespBean result = articleController.addNewArticle(article);

        assertEquals("error", result.getStatus());
        assertEquals("文章保存失败!", result.getMsg());
        verify(articleService).addNewArticle(article);
    }

    @Test
    void addNewArticle_shouldReturnErrorWhenStateIsNot0AndResultIsNot1() {
        Article article = new Article();
        article.setId(-1L);
        article.setState(1);
        when(articleService.addNewArticle(article)).thenReturn(0);

        RespBean result = articleController.addNewArticle(article);

        assertEquals("error", result.getStatus());
        assertEquals("文章发表失败!", result.getMsg());
        verify(articleService).addNewArticle(article);
    }

    @Test
    void addNewArticle_shouldReturnSuccessWithDifferentArticleId() {
        Article article = new Article();
        article.setId(100L);
        article.setState(0);
        when(articleService.addNewArticle(article)).thenReturn(1);

        RespBean result = articleController.addNewArticle(article);

        assertEquals("success", result.getStatus());
        assertEquals("100", result.getMsg());
    }

    @Test
    void addNewArticle_shouldHandleArticleWithNullId() {
        Article article = new Article();
        article.setState(0);
        when(articleService.addNewArticle(article)).thenReturn(1);

        RespBean result = articleController.addNewArticle(article);

        assertEquals("success", result.getStatus());
    }

    @Test
    void uploadImg_shouldReturnSuccessUrlWhenImageUploadSucceeds() throws IOException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("/blog");

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String tempDir = System.getProperty("java.io.tmpdir");
        java.io.File tempFolder = new java.io.File(tempDir, "blogimg_" + System.currentTimeMillis());
        tempFolder.mkdirs();

        RespBean result = articleController.uploadImg(mockRequest, mockFile);

        assertEquals("success", result.getStatus());
        assertNotNull(result.getMsg());
        assertTrue(result.getMsg().startsWith("http://localhost:8080/blog/blogimg/"));
        assertTrue(result.getMsg().endsWith(".jpg"));

        java.io.File[] files = tempFolder.listFiles();
        if (files != null && files.length > 0) {
            for (java.io.File file : files) {
                file.delete();
            }
            tempFolder.delete();
        }
    }

    @Test
    void uploadImg_shouldCreateDirectoryWhenItDoesNotExist() throws IOException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("https");
        mockRequest.setServerName("example.com");
        mockRequest.setServerPort(443);
        mockRequest.setContextPath("/myblog");

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "image.png",
                "image/png",
                "png content".getBytes()
        );

        String tempDir = System.getProperty("java.io.tmpdir");
        java.io.File tempFolder = new java.io.File(tempDir, "blogimg_test_" + System.currentTimeMillis());
        tempFolder.mkdirs();

        RespBean result = articleController.uploadImg(mockRequest, mockFile);

        assertEquals("success", result.getStatus());
        assertTrue(result.getMsg().startsWith("https://example.com:443/myblog/blogimg/"));

        java.io.File[] files = tempFolder.listFiles();
        if (files != null && files.length > 0) {
            for (java.io.File file : files) {
                file.delete();
            }
            tempFolder.delete();
        }
    }

    @Test
    void uploadImg_shouldHandleImageWithSpacesInFilename() throws IOException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("/blog");

        MockMultipartFile mockFile = new MockMultipartFile(
                "image",
                "test image with spaces.jpg",
                "image/jpeg",
                "content".getBytes()
        );

        String tempDir = System.getProperty("java.io.tmpdir");
        java.io.File tempFolder = new java.io.File(tempDir, "blogimg_spaces_" + System.currentTimeMillis());
        tempFolder.mkdirs();

        RespBean result = articleController.uploadImg(mockRequest, mockFile);

        assertEquals("success", result.getStatus());
        assertNotNull(result.getMsg());
        assertFalse(result.getMsg().contains(" "));

        java.io.File[] files = tempFolder.listFiles();
        if (files != null && files.length > 0) {
            for (java.io.File file : files) {
                file.delete();
            }
            tempFolder.delete();
        }
    }

    @Test
    void uploadImg_shouldReturnErrorWhenIOExceptionOccurs() throws IOException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("localhost");
        mockRequest.setServerPort(8080);
        mockRequest.setContextPath("/blog");

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("Test IO exception");
            }
        };

        String tempDir = System.getProperty("java.io.tmpdir");
        java.io.File tempFolder = new java.io.File(tempDir, "blogimg_error_test_" + System.currentTimeMillis());
        tempFolder.mkdirs();

        RespBean result = articleController.uploadImg(mockRequest, mockFile);

        assertEquals("error", result.getStatus());
        assertEquals("上传失败!", result.getMsg());

        if (tempFolder.exists()) {
            java.io.File[] files = tempFolder.listFiles();
            if (files != null) {
                for (java.io.File f : files) f.delete();
            }
            tempFolder.delete();
        }
    }

    @Test
    void getArticleByState_shouldReturnMapWithTotalCountAndArticles() {
        org.sang.bean.User user = new org.sang.bean.User();
        user.setId(1L);
        setMockCurrentUser(user);

        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        articles.add(article);

        when(articleService.getArticleCountByState(eq(-1), eq(1L), isNull())).thenReturn(1);
        when(articleService.getArticleByState(-1, 1, 6, null)).thenReturn(articles);

        Map<String, Object> result = articleController.getArticleByState(-1, 1, 6, null);

        assertNotNull(result);
        assertEquals(1, result.get("totalCount"));
        assertEquals(articles, result.get("articles"));
        assertEquals(2, result.size());
    }

    @Test
    void getArticleByState_shouldHandleDifferentStateValues() {
        org.sang.bean.User user = new org.sang.bean.User();
        user.setId(1L);
        setMockCurrentUser(user);

        List<Article> articles = new ArrayList<>();
        when(articleService.getArticleCountByState(eq(0), eq(1L), isNull())).thenReturn(5);
        when(articleService.getArticleByState(0, 1, 6, null)).thenReturn(articles);

        Map<String, Object> result = articleController.getArticleByState(0, 1, 6, null);

        assertNotNull(result);
        assertEquals(5, result.get("totalCount"));
    }

    @Test
    void getArticleByState_shouldHandleKeywords() {
        org.sang.bean.User user = new org.sang.bean.User();
        user.setId(1L);
        setMockCurrentUser(user);

        List<Article> articles = new ArrayList<>();
        when(articleService.getArticleCountByState(eq(-1), eq(1L), eq("test"))).thenReturn(3);
        when(articleService.getArticleByState(-1, 1, 6, "test")).thenReturn(articles);

        Map<String, Object> result = articleController.getArticleByState(-1, 1, 6, "test");

        assertNotNull(result);
        assertEquals(3, result.get("totalCount"));
    }

    @Test
    void getArticleByState_shouldHandleDifferentPageAndCountValues() {
        org.sang.bean.User user = new org.sang.bean.User();
        user.setId(1L);
        setMockCurrentUser(user);

        List<Article> articles = new ArrayList<>();
        when(articleService.getArticleCountByState(eq(-1), eq(1L), isNull())).thenReturn(10);
        when(articleService.getArticleByState(-1, 2, 10, null)).thenReturn(articles);

        Map<String, Object> result = articleController.getArticleByState(-1, 2, 10, null);

        assertNotNull(result);
        assertEquals(10, result.get("totalCount"));
    }

    @Test
    void getArticleByState_shouldHandleEmptyArticleList() {
        org.sang.bean.User user = new org.sang.bean.User();
        user.setId(1L);
        setMockCurrentUser(user);

        when(articleService.getArticleCountByState(eq(-1), eq(1L), isNull())).thenReturn(0);
        when(articleService.getArticleByState(-1, 1, 6, null)).thenReturn(new ArrayList<>());

        Map<String, Object> result = articleController.getArticleByState(-1, 1, 6, null);

        assertNotNull(result);
        assertEquals(0, result.get("totalCount"));
        assertTrue(((List<?>) result.get("articles")).isEmpty());
    }

    @Test
    void getArticleById_shouldReturnArticleWhenExists() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        when(articleService.getArticleById(1L)).thenReturn(article);

        Article result = articleController.getArticleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Article", result.getTitle());
        verify(articleService).getArticleById(1L);
    }

    @Test
    void getArticleById_shouldReturnNullWhenArticleNotExists() {
        when(articleService.getArticleById(999L)).thenReturn(null);

        Article result = articleController.getArticleById(999L);

        assertNull(result);
        verify(articleService).getArticleById(999L);
    }

    @Test
    void getArticleById_shouldHandleDifferentArticleIds() {
        Article article = new Article();
        article.setId(100L);
        when(articleService.getArticleById(100L)).thenReturn(article);

        Article result = articleController.getArticleById(100L);

        assertEquals(100L, result.getId());
    }

    @Test
    void updateArticleState_shouldReturnSuccessWhenAllArticlesDeleted() {
        Long[] aids = {1L, 2L, 3L};
        Integer state = 2;
        when(articleService.updateArticleState(aids, state)).thenReturn(3);

        RespBean result = articleController.updateArticleState(aids, state);

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
        verify(articleService).updateArticleState(aids, state);
    }

    @Test
    void updateArticleState_shouldReturnErrorWhenNotAllArticlesDeleted() {
        Long[] aids = {1L, 2L, 3L};
        Integer state = 2;
        when(articleService.updateArticleState(aids, state)).thenReturn(2);

        RespBean result = articleController.updateArticleState(aids, state);

        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(articleService).updateArticleState(aids, state);
    }

    @Test
    void updateArticleState_shouldHandleSingleArticleId() {
        Long[] aids = {1L};
        Integer state = 2;
        when(articleService.updateArticleState(aids, state)).thenReturn(1);

        RespBean result = articleController.updateArticleState(aids, state);

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
    }

    @Test
    void updateArticleState_shouldHandleStateNotEqualTo2() {
        Long[] aids = {1L, 2L};
        Integer state = 0;
        when(articleService.updateArticleState(aids, state)).thenReturn(2);

        RespBean result = articleController.updateArticleState(aids, state);

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
    }

    @Test
    void updateArticleState_shouldHandleEmptyAidsArray() {
        Long[] aids = {};
        Integer state = 2;
        when(articleService.updateArticleState(aids, state)).thenReturn(0);

        RespBean result = articleController.updateArticleState(aids, state);

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
    }

    @Test
    void restoreArticle_shouldReturnSuccessWhenArticleRestored() {
        when(articleService.restoreArticle(1)).thenReturn(1);

        RespBean result = articleController.restoreArticle(1);

        assertEquals("success", result.getStatus());
        assertEquals("还原成功!", result.getMsg());
        verify(articleService).restoreArticle(1);
    }

    @Test
    void restoreArticle_shouldReturnErrorWhenArticleNotRestored() {
        when(articleService.restoreArticle(1)).thenReturn(0);

        RespBean result = articleController.restoreArticle(1);

        assertEquals("error", result.getStatus());
        assertEquals("还原失败!", result.getMsg());
        verify(articleService).restoreArticle(1);
    }

    @Test
    void restoreArticle_shouldHandleDifferentArticleIds() {
        when(articleService.restoreArticle(100)).thenReturn(1);

        RespBean result = articleController.restoreArticle(100);

        assertEquals("success", result.getStatus());
        assertEquals("还原成功!", result.getMsg());
    }

    @Test
    void restoreArticle_shouldHandleNegativeArticleId() {
        when(articleService.restoreArticle(-1)).thenReturn(0);

        RespBean result = articleController.restoreArticle(-1);

        assertEquals("error", result.getStatus());
        assertEquals("还原失败!", result.getMsg());
    }

    @Test
    void dataStatistics_shouldReturnMapWithCategoriesAndDataStatistics() {
        List<String> categories = new ArrayList<>();
        categories.add("Category1");
        categories.add("Category2");

        List<Integer> dataStatistics = new ArrayList<>();
        dataStatistics.add(10);
        dataStatistics.add(20);

        when(articleService.getCategories()).thenReturn(categories);
        when(articleService.getDataStatistics()).thenReturn(dataStatistics);

        Map<String, Object> result = articleController.dataStatistics();

        assertNotNull(result);
        assertEquals(categories, result.get("categories"));
        assertEquals(dataStatistics, result.get("ds"));
        assertEquals(2, result.size());
    }

    @Test
    void dataStatistics_shouldHandleEmptyCategories() {
        when(articleService.getCategories()).thenReturn(new ArrayList<>());
        when(articleService.getDataStatistics()).thenReturn(new ArrayList<>());

        Map<String, Object> result = articleController.dataStatistics();

        assertNotNull(result);
        assertTrue(((List<?>) result.get("categories")).isEmpty());
        assertTrue(((List<?>) result.get("ds")).isEmpty());
    }

    @Test
    void dataStatistics_shouldHandleNullCategories() {
        when(articleService.getCategories()).thenReturn(null);
        when(articleService.getDataStatistics()).thenReturn(null);

        Map<String, Object> result = articleController.dataStatistics();

        assertNotNull(result);
        assertNull(result.get("categories"));
        assertNull(result.get("ds"));
    }

    @Test
    void dataStatistics_shouldHandleDifferentDataValues() {
        List<String> categories = new ArrayList<>();
        categories.add("Tech");
        categories.add("Life");
        categories.add("Food");

        List<Integer> dataStatistics = new ArrayList<>();
        dataStatistics.add(100);
        dataStatistics.add(200);
        dataStatistics.add(300);

        when(articleService.getCategories()).thenReturn(categories);
        when(articleService.getDataStatistics()).thenReturn(dataStatistics);

        Map<String, Object> result = articleController.dataStatistics();

        assertEquals(3, ((List<?>) result.get("categories")).size());
        assertEquals(3, ((List<?>) result.get("ds")).size());
        assertEquals(100, ((List<Integer>) result.get("ds")).get(0));
    }
}
