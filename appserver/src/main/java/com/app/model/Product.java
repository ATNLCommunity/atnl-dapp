package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

import org.apache.commons.lang3.StringUtils;

public class Product extends Model<Product>
{
    private static final long serialVersionUID = 3554755307155224113L;

    public static final Product dao = new Product();

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String USEQUAN = "usequan";
    public static final String OPRICE = "oprice";
    public static final String PRICE = "price";
    public static final String ETH = "eth";
    public static final String ATNL = "atnl";
    public static final String GIFT = "gift";
    public static final String LP = "lp";
    public static final String COUNT = "count";
    public static final String SEND_DATE = "send_date";
    public static final String NAME = "name";
    public static final String LOGO = "logo";
    public static final String DETAIL = "detail";
    public static final String URL = "url";
    public static final String YF = "yf";
    public static final String EXPIRE_TIME = "expire_time";
    public static final String SEND_DATE_DESC = "send_date_desc";
    public static final String SENDBY = "sendby";

    public Product create(Float oprice, Float price, Float eth, Float atnl, Float gift, Integer count, Integer type, String name, String logo, String detail, String url, String sendDate, String expireTime)
    {
        Product product = new Product();
        product.set(TYPE, type);
        product.set(OPRICE, oprice);
        product.set(PRICE, price);
        product.set(ETH, eth);
        product.set(ATNL, atnl);
        product.set(COUNT, count);
        product.set(TYPE, type);
        product.set(LOGO, logo);
        product.set(DETAIL, detail);
        product.set(URL, url);
        if (StringUtils.isBlank(sendDate))
        {
            sendDate = "2100-12-31";
        }
        product.set(SEND_DATE, sendDate);
        product.set(NAME, name);
        if (StringUtils.isBlank(expireTime))
        {
            expireTime = "2100-12-31";
        }
        product.set(EXPIRE_TIME, expireTime);

        if (product.save())
        {
            return product;
        }
        return null;
    }

    public List<Product> getProducts(Integer type)
    {
        if (type == -1)
        {
            return find("SELECT * FROM product WHERE expire_time>=NOW() AND count!=0 ORDER BY id DESC LIMIT 7");
        }
        return find("SELECT * FROM product WHERE type=? AND expire_time>=NOW() AND count!=0 ORDER BY id DESC", type);
    }

    public Product findProduct(Long pid)
    {
        return findFirst("SELECT * FROM product WHERE id=? AND expire_time>=NOW()", pid);
    }
}
