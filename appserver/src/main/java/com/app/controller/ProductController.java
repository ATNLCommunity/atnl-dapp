package com.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.model.Product;

import n.fw.base.BaseController;

public class ProductController extends BaseController
{
    public void list()
    {
        Integer type = getParaToInt("type", 0);
        success(Product.dao.getProducts(type));
    }

    public void all()
    {
        Map<String, List<Product>> map = new HashMap<String, List<Product>>();
        List<Product> type0 = Product.dao.getProducts(0);
        List<Product> type1 = Product.dao.getProducts(1);
        List<Product> type2 = Product.dao.getProducts(2);

        map.put("type0", type0);
        map.put("type1", type1);
        map.put("type2", type2);

        success(map);

    }

    public void get()
    {
        Long pid = getParaToLong("pid", 0L);
        if (pid == 0)
        {
            error("商品id不能为空");
            return;
        }

        success(Product.dao.findProduct(pid));
    }
}