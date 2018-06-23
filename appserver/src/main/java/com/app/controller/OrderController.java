package com.app.controller;

import java.util.List;

import com.app.model.Order;
import com.app.model.Product;
import com.app.model.Quan;
import com.app.model.Sheep;
import com.app.model.User;

import org.apache.commons.lang3.StringUtils;

import n.fw.base.BaseController;
import n.fw.utils.DateUtils;

public class OrderController extends BaseController
{
    public void create()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        Integer count = getParaToInt("count", 0);
        if (count <= 0)
        {
            error("商品数量不正常");
            return;
        }

        Integer payType = getParaToInt("payType", 1);   // 1为微信支付，2为ETH支付, 4支付宝, 3兑换
        String addr = getPara("addr", "");
        if (StringUtils.isBlank(addr))
        {
            error("请填写收货地址");
            return;
        }

        String name = getPara("name", "");
        if (StringUtils.isBlank(name))
        {
            error("请填写收货人姓名");
            return;
        }

        String msg = getPara("msg", "");

        Long pid = getParaToLong("pid", 0L);
        if (pid == 0)
        {
            error("请填写购买的商品");
            return;
        }

        String contacts = getPara("contacts", "");
        if (StringUtils.isBlank(contacts))
        {
            error("请填写联系人电话");
            return;
        }

        Product product = Product.dao.findProduct(pid);
        if (product == null)
        {
            error("找不到商品信息");
            return;
        }
        Integer productCount = product.getInt(Product.COUNT);
        if (productCount >= 0 && count > productCount)
        {
            error("商品数量超过库存");
            return;
        }
        if (product.getInt(Product.TYPE) != 0 && product.getInt(Product.TYPE) != 2)
        {
            error("该商品不能兑换");
            return;
        }
        
        Long qid = getParaToLong("qid", 0L);

        Float price = product.getFloat(Product.PRICE) * count + product.getFloat(Product.YF);
        Float realPrice = price;
        Float eth = product.getFloat(Product.ETH);

        Quan quan = null;
        if (product.getInt(Product.USEQUAN) != 0)
        {
            if (qid != 0L) 
            {
                if (count > 1)
                {
                    error("使用优惠券只能购买一个商品");
                    return;
                }
                quan = Quan.dao.findByQid(uid, qid);
                if (quan == null) 
                {
                    error("找不到优惠券");
                    return;
                }

                if (quan.getInt(Quan.STATE) > 0)
                {
                    Float discount = quan.getFloat(Quan.MONEY);
                    realPrice = price - discount;
                    if (realPrice < 0F)
                    {
                        realPrice = 0F;
                    }
                }
                else
                {
                    quan = null;
                }
            }
        }
        
        Order order = Order.dao.create(uid, pid, qid, count, price, realPrice, eth, payType, addr, name, contacts, msg);
        if (order == null)
        {
            error("创建失败");
            return;
        }

        if (quan != null)
        {
            quan.set(Quan.STATE, 0);
            quan.update();
        }

        if (realPrice <= 0F)
        {
            if (productCount > 0)
            {
                product.set(Product.COUNT, productCount - count);
                product.update();
            }
    
            order.set(Order.PAYMENT, 0);
            order.set(Order.PAYSTATE, 1);
            order.set(Order.STATE, 2);
            StringBuffer extra = new StringBuffer();
            if (pid == 1L)
            {
                List<Sheep> sheeps = Sheep.dao.getSheep(count);
                for (Sheep sheep : sheeps) {
                    if (extra.length() > 0) {
                        extra.append('|');
                    }
                    extra.append(sheep.getStr(Sheep.SID));
                    sheep.set(Sheep.STATE, 1);
                    sheep.update();
                }
            }
            order.set(Order.EXTRA, extra.toString());
            order.set(Order.PAY_TIME, DateUtils.getDateTime());
            order.update();
        }
        success(order);
    }

    public void mine()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        Integer type = getParaToInt("type", 0);

        success(Order.dao.getOrders(uid, type));
    }

    public void get()
    {
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0)
        {
            error("请输入订单id");
            return;
        }

        success(Order.dao.findDetailByOid(oid));
    }
    
	public void exchange()
	{
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0)
		{
			errorInvalidOper();
			return;
        }
        
        User user = User.dao.findByUid(uid);
        if (user == null)
        {
            errorInvalidOper();
            return;
        }

		Long pid = getParaToLong("pid", 0L);
		if (pid == 0L)
		{
            error("请填写购买的商品");
			return;
		}
        
        Integer count = getParaToInt("count", 0);
        if (count <= 0)
        {
            error("商品数量不正常");
            return;
        }

        String contacts = getPara("contacts", "");
        if (StringUtils.isBlank(contacts))
        {
            error("请填写联系人电话");
            return;
        }

        Integer payType = 3;   // 1为微信支付，2为ETH支付, 3为兑换
        String addr = getPara("addr", "");
        if (StringUtils.isBlank(addr))
        {
            error("请填写收货地址");
            return;
        }

        String name = getPara("name", "");
        if (StringUtils.isBlank(name))
        {
            error("请填写收货人姓名");
            return;
        }

        String msg = getPara("msg", "");

        Product product = Product.dao.findProduct(pid);
        if (product == null)
        {
            error("找不到商品信息");
            return;
        }
        
        if (product.getInt(Product.TYPE) != 1)
        {
            error("该商品不能兑换");
            return;
        }

        Integer productCount = product.getInt(Product.COUNT);
        if (productCount == 0)
        {
            error("商品已经售完");
            return;
        }
        
        Float price = product.getFloat(Product.PRICE) * count + product.getFloat(Product.YF);
        Float realPrice = price;
        Float eth = product.getFloat(Product.ETH);
        Float atnl = product.getFloat(Product.ATNL);
        Float userAtnl = user.getFloat(User.ATNL);
        if (atnl * count > userAtnl)
        {
            error("您的ATNL不足");
            return;
        }

        Order order = Order.dao.create(uid, pid, 0L, count, price, realPrice, eth, payType, addr, name, contacts, msg);
        if (order == null)
        {
            error("创建失败");
            return;
        }

        if (realPrice <= 0F)
        {
            if (productCount > 0)
            {
                product.set(Product.COUNT, productCount - count);
                product.update();
            }
    
            order.set(Order.ATNLPAY, atnl * count);
            order.set(Order.PAYSTATE, 1);
            order.set(Order.STATE, 2);
            StringBuffer extra = new StringBuffer();
            if (pid == 1L)
            {
                List<Sheep> sheeps = Sheep.dao.getSheep(count);
                for (Sheep sheep : sheeps) {
                    if (extra.length() > 0) {
                        extra.append('|');
                    }
                    extra.append(sheep.getStr(Sheep.SID));
                    sheep.set(Sheep.STATE, 1);
                    sheep.update();
                }
            }
            order.set(Order.EXTRA, extra.toString());
            order.set(Order.PAY_TIME, DateUtils.getDateTime());
            order.update();

            Float gift = product.getFloat(Product.GIFT);
            if (gift > 0)
            {
                user.set(User.ATNL, userAtnl - (atnl * count) + gift);
            }
            else
            {
                user.set(User.ATNL, userAtnl - (atnl * count));
            }
            Float lp = product.getFloat(Product.LP);
            if (lp > 0)
            {
                user.set(User.LP, user.getFloat(User.LP) + lp);
            }
            user.set(User.ALLATNL, user.getFloat(User.ALLATNL) + atnl * count);
            user.update();
        }
        else
        {
            order.set(Order.ATNLPAY, atnl * count);
            System.out.println("set atnlpay = " + atnl * count);
            order.update();
            user.set(User.ATNL, userAtnl - (atnl * count));
            user.set(User.ALLATNL, user.getFloat(User.ALLATNL) + atnl * count);
            user.update();
        }
 
        success(order);
    }
    
    public void isPayed()
    {
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0)
        {
            success(1);
            return;
        }
        
        Order order = Order.dao.findByOid(oid);
        if (order == null || order.getInt(Order.PAYSTATE) == 0)
        {
            success(0);
            return;
        }
        else 
        {
            success(1);
            return;
        }
    }
    public void close()
    {
    	Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0)
		{
			errorInvalidOper();
			return;
        }
        
        User user = User.dao.findByUid(uid);
        if (user == null)
        {
            errorInvalidOper();
            return;
        }
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0)
        {
            error("找不到该订单");
            return;
        }

        Order order = Order.dao.findByOid(oid);
        if (order == null)
        {
            error("找不到该订单");
            return;
        }

        if (order.getInt(Order.STATE) != 1)
        {
            error("订单状态不正确");
            return;
        }

        Float atnlPay = order.getFloat(Order.ATNLPAY);
        order.set(Order.STATE, 0);
        order.set(Order.ATNLPAY, 0);
        order.update();

        if (atnlPay > 0) {
            Float userAtnl = user.getFloat(User.ATNL);
            user.set(User.ATNL, userAtnl + atnlPay);
            user.update();
        }
       
        success(order);
    }
}