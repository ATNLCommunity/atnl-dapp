package com.app.controller;



import java.util.HashMap;
import java.util.List;

import com.app.model.BasicRecord;
import com.app.model.Goods;
import com.app.model.Sheep;
import com.app.model.StepRecord;

import n.fw.base.BaseController;

public class GoodsController extends BaseController
{
  
    public void get()
    {
        Long gid = getParaToLong("gid", 0l);
		if (gid <= 0)
        {
			error("商品id不能为空");
            return;
        }
        success(Goods.dao.findById(gid));
    }
    
    public void getByOid()
    {
        Long oid = getParaToLong("oid", 0l);
		if (oid <= 0)
        {
			error("商品id不能为空");
            return;
        }
        success(Goods.dao.findByOid(oid));
    }
    
    public void showInfo()
    {
    	Long gid = getParaToLong("gid", 0l);
		if (gid <= 0)
        {
			error("商品id不能为空");
            return;
        }
    	Goods goods = Goods.dao.findById(gid);
    	
    	if(null == goods)
    	{
    		error("商品信息不存在");
            return;
    	}
    	
    	Sheep sheep = Sheep.dao.findById(goods.getLong(Goods.SHEEPID));
    	List<BasicRecord> list = BasicRecord.dao.getAll(goods.getLong(Goods.SHEEPID));
    	Long sumSteps = StepRecord.dao.getSum(goods.getLong(Goods.SHEEPID));
    	
    	HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("goods", goods);
        map.put("sheep", sheep);
        map.put("blist", list);
        map.put("sum", sumSteps);
        
    	success(map);
    	
    }
   
}