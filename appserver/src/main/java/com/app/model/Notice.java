package com.app.model;


import java.util.List;

import com.jfinal.plugin.activerecord.Model;


// 后台公告表
public class Notice extends Model<Notice>
{
    private static final long serialVersionUID = 3554755307155224108L;

    public static final Notice dao = new Notice();

    public static final String ID = "id";       // ID
    public static final String AID = "aid"; // 发公告的管理员id
    public static final String SENDTIME = "sendtime";   // 发送时间
    public static final String TIILE = "title";     // 标题
    public static final String CONTENT = "content"; //内容

    
    
    public List<Notice> list(int page)
    {
    	return find("select * from notice order by id desc limit ?,20",page*20);
    }
    public Notice getByNid(Long nid)
    {
    	return findFirst("select * from notice where id = ?",nid);
    }
}
