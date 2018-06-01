package n.fw.utils;

import java.util.HashMap;
import java.util.Random;

public class CacheUtils
{
    public static final CacheUtils instance = new CacheUtils();

    private HashMap<String, String> mCacheMap = new HashMap<String,String>();

    static final Random mRandom = new Random();

    public static String rand()
    {
        int rd = mRandom.nextInt(1000000);
        if (rd < 100000)
        {
            rd += 100000;
        }
        return Integer.toString(rd);
    }

    public void clear()
    {
        mCacheMap.clear();
    }

    public void set(String key, String value)
    {
        if (value == null)
        {
            mCacheMap.remove(key);
        }
        else
        {
            mCacheMap.put(key, value);
        }
    }

    public String get(String key)
    {
        if (mCacheMap.containsKey(key))
        {
            return mCacheMap.get(key);
        }
        else
        {
            return null;
        }
    }
}