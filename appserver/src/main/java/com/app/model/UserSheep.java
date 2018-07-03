package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;


//用户云养羊关系表
public class UserSheep extends Model<UserSheep> 
{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8924220277713593441L;
	public static final UserSheep dao = new UserSheep();
	public static final String ID = "id";   // 主键
	public static final String UID = "uid"; //用户ID
	public static final String SHEEPID = "sheepid"; // 实体羊id
	public static final String PAYSTATUS = "paystatus"; // 购买状态0未支付1已支付
	public static final String ISOUT = "isout";   // 是否收获0未收获1已收获
	public static final String ADDTIME = "addtime";   // 是否收获0未收获1已收获
	public static final String PAYTIME = "paytime";   // 是否收获0未收获1已收获
	public static final String PAYMONEY = "paymoney";   // 支付金额

	public UserSheep create(Long uid, Long sheepid, int paystatus, int isout,Float paymoney)
    {
		UserSheep userSheep = new UserSheep();
        userSheep.set(UID, uid);
        userSheep.set(SHEEPID, sheepid);
        userSheep.set(PAYSTATUS, paystatus);
        userSheep.set(ISOUT, isout);
        String addtime = DateUtils.getDateTime();
        userSheep.set(ADDTIME, addtime);
        userSheep.set(PAYTIME, addtime);
        userSheep.set(PAYMONEY, paymoney);
        if (userSheep.save())
        {
            return userSheep;
        }
        return null;
    }

    public List<Object> getUserSheeps(Long uid)
    {
        return Db.query("SELECT * FROM `usersheep` as a left join sheep as b on a.sheepid = b.id WHERE a.uid=? ", uid);
    }

    public UserSheep findByOid(Long oid)
    {
        return findFirst("SELECT * FROM `usersheep` WHERE id=?", oid);
    }
    
    public UserSheep findBySid(Long sheepid)
    {
        return findFirst("SELECT * FROM `usersheep` WHERE sheepid=?", sheepid);
    }

    public UserSheep findByUid(Long uid)
    {
        return findFirst("SELECT * FROM `usersheep` WHERE uid=? and paystatus=0", uid);
    }
	
}
