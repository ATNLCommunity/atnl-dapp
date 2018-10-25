package com.app.model;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class Apply extends Model<Apply> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8968813007010578000L;

	public static final Apply dao = new Apply();

	public static final String ID = "id";
    public static final String USERID = "userid";
    public static final String CONTACT = "contact";
    public static final String TYPE = "type";
    public static final String CONTENT = "content";
    public static final String CREATETIME = "createtime";
    public static final String STATE = "state";
    public static final String CHECKID = "checkid";
    public static final String CHECKTIME = "checktime";
    public static final String NAME = "name";
	
    public Apply create(Long userid,String contact,int type,String name)
    {
    	Apply apply = new Apply();
    	apply.set(USERID, userid);
    	apply.set(CONTACT, contact);
    	apply.set(TYPE, type);
    	apply.set(STATE, 0);
    	apply.set(CREATETIME, DateUtils.getDateTime());
    	apply.set(NAME, name);

        if (apply.save())
        {
            return apply;
        }
        return null;
    }

}
