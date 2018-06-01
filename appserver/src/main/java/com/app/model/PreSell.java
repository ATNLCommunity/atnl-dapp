package com.app.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class PreSell extends Model<PreSell>
{
    private static final long serialVersionUID = 3554755307155224122L;

    public static final PreSell dao = new PreSell();

    public static final String PHONE = "phone";
    public static final String STATE = "state";

    public PreSell getPreSell(String phone)
    {
        return findFirst("SELECT * FROM presell WHERE phone=? LIMIT 1", phone);
    }

    public void update(String phone, Integer state)
    {
        Db.update("UPDATE presell SET state=? WHERE phone=? LIMIT 1", state, phone);
    }
}