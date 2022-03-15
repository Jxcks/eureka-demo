package com.xxxx.service.impl;

import com.xxxx.pojo.Product;
import com.xxxx.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    /**
     * 查询商品列表
     * @return
     */
    @Override
    public List<Product> selectProductList() {
        return Arrays.asList(
                new Product(1, "华为手机", 2, 8888D),
                new Product(2, "联想笔记本", 1, 8999D),
                new Product(3, "ipad", 5, 12500D)
        );
    }
}
