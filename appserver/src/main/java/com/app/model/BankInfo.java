package com.app.model;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;


public class BankInfo extends Model<BankInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8968813007010578000L;

	public static final BankInfo dao = new BankInfo();

	public static final String ID = "id";
    public static final String UID = "uid";
    public static final String BANKNAME = "bankname";
    public static final String BANKNO = "bankno";
    public static final String ONAME = "oname";
    
	
    public BankInfo create(Long userid,String bankname,String bankno,String oname)
    {
    	BankInfo bankinfo = new BankInfo();
    	bankinfo.set(UID, userid);
    	bankinfo.set(BANKNAME, bankname);
    	bankinfo.set(BANKNO, bankno);
    	bankinfo.set(ONAME, oname);

        if (bankinfo.save())
        {
            return bankinfo;
        }
        return null;
    }
    
    public List<BankInfo> getList(Long uid)
    {
    	return find("Select * from bankinfo where uid = ?",uid);
    }

}
