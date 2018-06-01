package com.app.model;

import com.jfinal.plugin.activerecord.Model;

public class Invite extends Model<Invite>
{
    private static final long serialVersionUID = 3554755307155224123L;

    public static final Invite dao = new Invite();

    public static final String ID = "id";
    public static final String REG = "reg";
    public static final String INVITE = "invite";
    public static final String COUNT = "count";

    public Invite get()
    {
        return findFirst("SELECT * FROM invite ORDER BY id DESC LIMIT 1");
    }
}