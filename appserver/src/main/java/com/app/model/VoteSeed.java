package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class VoteSeed extends Model<VoteSeed>
{
    private static final long serialVersionUID = 3554755307155224125L;

    public static final VoteSeed dao = new VoteSeed();

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String RATE = "rate";
    public static final String LOGO = "logo";

    public List<VoteSeed> getAll()
    {
        return find("SELECT * FROM voteseed");
    }
}