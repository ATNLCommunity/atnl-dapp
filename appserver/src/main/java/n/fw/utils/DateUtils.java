package n.fw.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jerry on 2016/5/18.
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static String getDate()
    {
        return getDate("yyyy-MM-dd");
    }

    public static String getDate(String pattern)
    {
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static String getDate(Date date, String pattern)
    {
        return DateFormatUtils.format(date, pattern);
    }

    public static String formatDate(Date date, Object pattern[])
    {
        String formatDate = null;
        if (pattern != null && pattern.length > 0)
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        else
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        return formatDate;
    }

    public static String formatDateTime(Date date)
    {
        return formatDate(date, new Object[] { "yyyy-MM-dd HH:mm:ss" });
    }

    public static String getTime()
    {
        return formatDate(new Date(), new Object[] { "HH:mm:ss" });
    }

    public static String getDateTime()
    {
        return formatDate(new Date(), new Object[] { "yyyy-MM-dd HH:mm:ss" });
    }

    public static String getYear()
    {
        return formatDate(new Date(), new Object[] { "yyyy" });
    }

    public static String getMonth()
    {
        return formatDate(new Date(), new Object[] { "MM" });
    }

    public static String getDay()
    {
        return formatDate(new Date(), new Object[] { "dd" });
    }

    public static String getWeek()
    {
        return formatDate(new Date(), new Object[] { "E" });
    }

    public static Date parseDate(Object str)
    {
        if (str == null)
            return null;
        try
        {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e)
        {
            return null;
        }
    }

    public static long pastDays(Date date)
    {
        long t = (new Date()).getTime() - date.getTime();
        return t / 86400000L;
    }

    public static Date getDateStart(Date date)
    {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            date = sdf.parse((new StringBuilder()).append(formatDate(date, new Object[] { "yyyy-MM-dd" })).append(" 00:00:00").toString());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateEnd(Date date)
    {
        if (date == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            date = sdf.parse((new StringBuilder()).append(formatDate(date, new Object[] { "yyyy-MM-dd" })).append(" 23:59:59").toString());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getFirstDayOfWeek(Date date)
    {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(2);
        c.setTime(date);
        c.set(7, c.getFirstDayOfWeek());
        return c.getTime();
    }

    public static Date getLastDayOfWeek(Date date)
    {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(2);
        c.setTime(date);
        c.set(7, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    public static int getWeekNumber()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(2);
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeekNumber(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(2);
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
    
    private static String parsePatterns[] = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };

}
