package com.app.controller;

import com.app.model.Product;

import n.fw.base.BaseController;

public class ProductController extends BaseController
{
    public void list()
    {
        Integer type = getParaToInt("type", 0);
        success(Product.dao.getProducts(type));
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