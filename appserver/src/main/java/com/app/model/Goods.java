package com.app.model;

import com.jfinal.plugin.activerecord.Model;

public class Goods extends Model<Goods>
{

	private static final long serialVersionUID = 8612875850781959317L;

	public static final Goods dao = new Goods();

	
	
    public static final String GID = "id";  //商品id
    public static final String ORDERID = "orderid";//对应订单id
    public static final String SHEEPID = "sheepid";//源羊id
    public static final String BLOCKURL = "blockurl";//溯源链接
    public static final String GICON = "gicon";//显示图片
    public static final String KILLTIME = "killtime";//加工时间
    public static final String WORKPHOTO = "workphoto";//加工图片
    public static final String PACKTIME = "packtime";//打包时间
    public static final String PACKPHOTO = "packphoto";  //打包图片
    
    
    public Goods findById(String gid)
    {
        return findFirst("SELECT * FROM goods WHERE id=? LIMIT 1", gid);
    }
    
    public Goods findByOid(long oid)
    {
        return findFirst("SELECT * FROM goods WHERE orderid=? LIMIT 1", oid);
    }

    
}