package org.csu.mypetstore.api;

import org.csu.mypetstore.api.entity.Category;
import org.csu.mypetstore.api.persistence.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@MapperScan("org.csu.mypetstore.api.persistence")
class MypetstoreApiDemoApplicationTests {

    @Autowired
    private CategoryMapper categoryMapper;
    @Test
    void contextLoads() {
    }

    @Test
    void testCategory(){
        List<Category> categoryList = categoryMapper.selectList(null);
        System.out.println(categoryList);
        System.out.println(categoryList.size());
    }

}
