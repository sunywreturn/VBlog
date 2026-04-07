package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sang.bean.Category;
import org.sang.bean.RespBean;
import org.sang.service.CategoryService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryController = new CategoryController();
        categoryController.categoryService = categoryService;
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        List<Category> categories = new ArrayList<>();
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setCateName("Tech");
        cat1.setDate(new Timestamp(System.currentTimeMillis()));
        categories.add(cat1);
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setCateName("Life");
        cat2.setDate(new Timestamp(System.currentTimeMillis()));
        categories.add(cat2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        List<Category> result = categoryController.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryService).getAllCategories();
    }

    @Test
    void getAllCategories_shouldReturnEmptyListWhenNoCategories() {
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());

        List<Category> result = categoryController.getAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryService).getAllCategories();
    }

    @Test
    void getAllCategories_shouldReturnNullWhenServiceReturnsNull() {
        when(categoryService.getAllCategories()).thenReturn(null);

        List<Category> result = categoryController.getAllCategories();

        assertNull(result);
        verify(categoryService).getAllCategories();
    }

    @Test
    void deleteById_shouldReturnSuccessRespBeanWhenDeletionSucceeds() {
        when(categoryService.deleteCategoryByIds("1,2,3")).thenReturn(true);

        RespBean result = categoryController.deleteById("1,2,3");

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
        verify(categoryService).deleteCategoryByIds("1,2,3");
    }

    @Test
    void deleteById_shouldReturnErrorRespBeanWhenDeletionFails() {
        when(categoryService.deleteCategoryByIds("1,2,3")).thenReturn(false);

        RespBean result = categoryController.deleteById("1,2,3");

        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(categoryService).deleteCategoryByIds("1,2,3");
    }

    @Test
    void deleteById_shouldHandleSingleId() {
        when(categoryService.deleteCategoryByIds("1")).thenReturn(true);

        RespBean result = categoryController.deleteById("1");

        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
    }

    @Test
    void deleteById_shouldHandleEmptyIds() {
        when(categoryService.deleteCategoryByIds("")).thenReturn(false);

        RespBean result = categoryController.deleteById("");

        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
    }

    @Test
    void addNewCate_shouldReturnErrorRespBeanWhenCateNameIsEmpty() {
        Category category = new Category();
        category.setCateName("");

        RespBean result = categoryController.addNewCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("请输入栏目名称!", result.getMsg());
        verify(categoryService, never()).addCategory(any());
    }

    @Test
    void addNewCate_shouldReturnErrorRespBeanWhenCateNameIsNull() {
        Category category = new Category();
        category.setCateName(null);

        RespBean result = categoryController.addNewCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("请输入栏目名称!", result.getMsg());
        verify(categoryService, never()).addCategory(any());
    }

    @Test
    void addNewCate_shouldReturnSuccessRespBeanWhenAddSucceeds() {
        Category category = new Category();
        category.setCateName("NewCategory");
        when(categoryService.addCategory(category)).thenReturn(1);

        RespBean result = categoryController.addNewCate(category);

        assertEquals("success", result.getStatus());
        assertEquals("添加成功!", result.getMsg());
        verify(categoryService).addCategory(category);
    }

    @Test
    void addNewCate_shouldReturnErrorRespBeanWhenAddFails() {
        Category category = new Category();
        category.setCateName("NewCategory");
        when(categoryService.addCategory(category)).thenReturn(0);

        RespBean result = categoryController.addNewCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("添加失败!", result.getMsg());
        verify(categoryService).addCategory(category);
    }

    @Test
    void addNewCate_shouldHandleValidCateName() {
        Category category = new Category();
        category.setCateName("ValidCategory");
        when(categoryService.addCategory(category)).thenReturn(1);

        RespBean result = categoryController.addNewCate(category);

        assertEquals("success", result.getStatus());
        assertEquals("添加成功!", result.getMsg());
    }

    @Test
    void updateCate_shouldReturnSuccessRespBeanWhenUpdateSucceeds() {
        Category category = new Category();
        category.setId(1L);
        category.setCateName("UpdatedCategory");
        when(categoryService.updateCategoryById(category)).thenReturn(1);

        RespBean result = categoryController.updateCate(category);

        assertEquals("success", result.getStatus());
        assertEquals("修改成功!", result.getMsg());
        verify(categoryService).updateCategoryById(category);
    }

    @Test
    void updateCate_shouldReturnErrorRespBeanWhenUpdateFails() {
        Category category = new Category();
        category.setId(1L);
        category.setCateName("UpdatedCategory");
        when(categoryService.updateCategoryById(category)).thenReturn(0);

        RespBean result = categoryController.updateCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("修改失败!", result.getMsg());
        verify(categoryService).updateCategoryById(category);
    }

    @Test
    void updateCate_shouldHandleCategoryWithNullId() {
        Category category = new Category();
        category.setCateName("CategoryWithoutId");
        when(categoryService.updateCategoryById(category)).thenReturn(0);

        RespBean result = categoryController.updateCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("修改失败!", result.getMsg());
    }

    @Test
    void updateCate_shouldHandleCategoryWithEmptyCateName() {
        Category category = new Category();
        category.setId(1L);
        category.setCateName("");
        when(categoryService.updateCategoryById(category)).thenReturn(0);

        RespBean result = categoryController.updateCate(category);

        assertEquals("error", result.getStatus());
        assertEquals("修改失败!", result.getMsg());
    }
}
