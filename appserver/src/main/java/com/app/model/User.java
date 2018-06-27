package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class User extends Model<User> {
    private static final long serialVersionUID = 3554755307155224112L;

    public static final User dao = new User();

    public static final String ID = "id";
    public static final String INVITEID = "inviteid";
    public static final String PHONE = "phone";
    public static final String PWD = "pwd";
    public static final String NAME = "name";
    public static final String MAIL = "mail";
    public static final String TOKENADDR = "tokenaddr";
    public static final String MONEY = "money";
    public static final String ATNL = "atnl";
    public static final String LOCKATNL = "lockatnl";
    public static final String NEEDATNL = "needatnl";
    public static final String ALLATNL = "allatnl";
    public static final String LP = "lp";
    public static final String IP = "ip";
    public static final String STATE = "state";
    public static final String M1 = "m1";
    public static final String M2 = "m2";
    public static final String M3 = "m3";
    public static final String M4 = "m4";
    public static final String M5 = "m5";
    public static final String M6 = "m6";
    public static final String M21 = "m21";
    public static final String UPDATE_TIME = "update_time";
    public static final String CREATE_TIME = "create_time";

    public User create(String phone, String pwd, String mail, String tokenaddr, Long inviteid, Float gift, String ip) {
        User user = new User();
        user.set(INVITEID, inviteid);
        user.set(PHONE, phone);
        user.set(PWD, pwd);
        user.set(NAME, "牧友_" + phone.substring(7));
        user.set(MAIL, mail);
        user.set(TOKENADDR, tokenaddr);
        user.set(MONEY, 0);
        user.set(ATNL, 0);
        user.set(LP, 0);
        user.set(STATE, 0);
        user.set(LOCKATNL, gift);
        user.set(NEEDATNL, 0);
        user.set(ALLATNL, 0);
        user.set(IP, ip);
        String time = DateUtils.getDateTime();
        user.set(UPDATE_TIME, time);
        user.set(CREATE_TIME, time);
        if (user.save()) {
            return user;
        }

        return null;
    }

    public User findByPhone(String phone) {
        return findFirst("SELECT * FROM user WHERE phone=?", phone);
    }

    public User findByUid(Long uid) {
        return findFirst("SELECT * FROM user WHERE id=?", uid);
    }

    public List<User> findInvites(Long uid)
    {
        return find("SELECT id,phone,name,update_time FROM user WHERE inviteid=?", uid);
    }
}