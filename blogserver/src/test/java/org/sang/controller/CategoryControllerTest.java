package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sang.bean.Category;
import org.sang.bean.RespBean;
import org.sang.service.CategoryService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCateName("测试分类");
        testCategory.setDate(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void getAllCategories_shouldReturnCategoryList_whenServiceReturnsList() {
        List<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(testCategory);
        
        when(categoryService.getAllCategories()).thenReturn(expectedCategories);

        List<Category> result = categoryController.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory.getId(), result.get(0).getId());
        assertEquals(testCategory.getCateName(), result.get(0).getCateName());
        verify(categoryService).getAllCategories();
    }

    @Test
    void getAllCategories_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        List<Category> expectedCategories = new ArrayList<>();
        
        when(categoryService.getAllCategories()).thenReturn(expectedCategories);

        List<Category> result = categoryController.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryService).getAllCategories();
    }

    @Test
    void deleteById_shouldReturnSuccessRespBean_whenDeleteSucceeds() {
        String ids = "1,2,3";
        when(categoryService.deleteCategoryByIds(ids)).thenReturn(true);

        RespBean result = categoryController.deleteById(ids);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("删除成功!", result.getMsg());
        verify(categoryService).deleteCategoryByIds(ids);
    }

    @Test
    void deleteById_shouldReturnErrorRespBean_whenDeleteFails() {
        String ids = "1,2,3";
        when(categoryService.deleteCategoryByIds(ids)).thenReturn(false);

        RespBean result = categoryController.deleteById(ids);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(categoryService).deleteCategoryByIds(ids);
    }

    @Test
    void deleteById_shouldReturnErrorRespBean_whenIdsIsEmpty() {
        String ids = "";
        when(categoryService.deleteCategoryByIds(ids)).thenReturn(false);

        RespBean result = categoryController.deleteById(ids);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("删除失败!", result.getMsg());
        verify(categoryService).deleteCategoryByIds(ids);
    }

    @Test
    void addNewCate_shouldReturnSuccessRespBean_whenCategoryIsValidAndAddSucceeds() {
        when(categoryService.addCategory(testCategory)).thenReturn(1);

        RespBean result = categoryController.addNewCate(testCategory);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("添加成功!", result.getMsg());
        verify(categoryService).addCategory(testCategory);
    }

    @Test
    void addNewCate_shouldReturnErrorRespBean_whenCategoryIsValidButAddFails() {
        when(categoryService.addCategory(testCategory)).thenReturn(0);

        RespBean result = categoryController.addNewCate(testCategory);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("添加失败!", result.getMsg());
        verify(categoryService).addCategory(testCategory);
    }

    @Test
    void addNewCate_shouldReturnErrorRespBean_whenCateNameIsEmpty() {
        Category categoryWithEmptyName = new Category();
        categoryWithEmptyName.setCateName("");

        RespBean result = categoryController.addNewCate(categoryWithEmptyName);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("请输入栏目名称!", result.getMsg());
        verify(categoryService, never()).addCategory(any(Category.class));
    }

    @Test
    void addNewCate_shouldReturnErrorRespBean_whenCateNameIsNull() {
        Category categoryWithNullName = new Category();
        categoryWithNullName.setCateName(null);

        RespBean result = categoryController.addNewCate(categoryWithNullName);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("请输入栏目名称!", result.getMsg());
        verify(categoryService, never()).addCategory(any(Category.class));
    }

    @Test
    void addNewCate_shouldReturnErrorRespBean_whenServiceReturnsNegativeValue() {
        when(categoryService.addCategory(testCategory)).thenReturn(-1);

        RespBean result = categoryController.addNewCate(testCategory);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("添加失败!", result.getMsg());
        verify(categoryService).addCategory(testCategory);
    }

    @Test
    void updateCate_shouldReturnSuccessRespBean_whenUpdateSucceeds() {
        when(categoryService.updateCategoryById(testCategory)).thenReturn(1);

        RespBean result = categoryController.updateCate(testCategory);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals("修改成功!", result.getMsg());
        verify(categoryService).updateCategoryById(testCategory);
    }

    @Test
    void updateCate_shouldReturnErrorRespBean_whenUpdateFails() {
        when(categoryService.updateCategoryById(testCategory)).thenReturn(0);

        RespBean result = categoryController.updateCate(testCategory);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("修改失败!", result.getMsg());
        verify(categoryService).updateCategoryById(testCategory);
    }

    @Test
    void updateCate_shouldReturnErrorRespBean_whenServiceReturnsNegativeValue() {
        when(categoryService.updateCategoryById(testCategory)).thenReturn(-1);

        RespBean result = categoryController.updateCate(testCategory);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals("修改失败!", result.getMsg());
        verify(categoryService).updateCategoryById(testCategory);
    }
}