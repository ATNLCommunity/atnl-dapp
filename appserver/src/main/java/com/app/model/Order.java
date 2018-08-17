package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class Order extends Model<Order>
{
    private static final long serialVersionUID = 3554755307155224114L;

    public static final Order dao = new Order();

    public static final String ID = "id";
    public static final String UID = "uid";
    public static final String PID = "pid";
    public static final String QID = "qid";
    public static final String COUNT = "count";
    public static final String PRICE = "price";
    public static final String REALPRICE = "realprice";
    public static final String ETH = "eth";
    public static final String PAYMENT = "payment";
    public static final String ATNLPAY = "atnlpay";
    public static final String PAYTYPE = "paytype";
    public static final String PAYSTATE = "paystate";//
    public static final String STATE = "state";//
    public static final String CREATE_TIME = "create_time";
    public static final String PAY_TIME = "pay_time";
	public static final String SEND_TIME = "send_time";
    public static final String ADDR = "addr";
    public static final String NAME = "name";
    public static final String CONTACTS = "contacts";
    public static final String MSG = "msg";
	public static final String TRACHNO = "trackno";
    public static final String EXTRA = "extra";

    public Order create(Long uid, Long pid, Long qid, Integer count, Float price, Float realprice, Float eth, int payType, String addr, String name, String contacts, String msg)
    {
        Order order = new Order();
        order.set(UID, uid);
        order.set(PID, pid);
        order.set(QID, qid);
        order.set(COUNT, count);
        order.set(PRICE, price);
        order.set(ATNLPAY, 0.0F);
        order.set(PAYMENT, 0.0F);
        order.set(REALPRICE, realprice);
        order.set(ETH, eth);
        order.set(PAYTYPE, payType);
        order.set(ADDR, addr);
        order.set(NAME, name);
        order.set(CONTACTS, contacts);
        order.set(MSG, msg);
        order.set(STATE, 1);
        order.set(CREATE_TIME, DateUtils.getDateTime());

        if (order.save())
        {
            return order;
        }
        return null;
    }

    public List<Order> getOrders(Long uid, int type)
    {
        if (type == 0)
        {
            return find("SELECT `order`.*,product.price AS pprice,product.eth AS peth,product.count AS pcount,product.send_date,product.name AS pname,product.expire_time,product.logo,product.atnl,product.detail,product.url FROM `order` INNER JOIN product ON product.id=`order`.pid WHERE `order`.uid=? AND `order`.state>0", uid);
        }
        else
        {
            return find("SELECT `order`.*,product.price AS pprice,product.eth AS peth,product.count AS pcount,product.send_date,product.name AS pname,product.expire_time,product.logo,product.atnl,product.detail,product.url FROM `order` INNER JOIN product ON product.id=`order`.pid WHERE `order`.uid=? AND `order`.paytype=? AND `order`.state>0", uid, type);
        }
    }

    public Order findDetailByOid(Long oid)
    {
        return findFirst(
                "SELECT `order`.*,product.price AS pprice,product.eth AS peth,product.count AS pcount,product.send_date,product.name AS pname,product.expire_time,product.logo,product.atnl,product.detail,product.url FROM `order` INNER JOIN product ON product.id=`order`.pid WHERE `order`.id=?",
                oid);
    }

    public Order findByOid(Long oid)
    {
        return findFirst("SELECT * FROM `order` WHERE id=? LIMIT 1", oid);
    }
    
    /**
     * 万羊活动订单统计
     * @return
     */
    public int getOrderCount()
    {
    	return Db.queryLong("SELECT count(*) FROM `order` WHERE pid=39 and paystate = 1 and pay_time > '2018-08-19'").intValue();
    }
}