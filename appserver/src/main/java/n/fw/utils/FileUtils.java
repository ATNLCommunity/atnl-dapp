package n.fw.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils
{
    public static final FileUtils instance = new FileUtils();

    /**
     * <文件转换成byte数组>
     * <功能详细描述>
     * @param sPath
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] changeFileToByte(String sPath)
    {
        byte[] buffer = null;
        try
        {
            File file = new File(sPath);
            if (!file.exists())
            {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            
            byte[] b = new byte[1024];
            int n;
            //每次从fis读1000个长度到b中，fis中读完就会返回-1
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return buffer;
    }
    /**
     * byte[] 写入文件
     * @param spath
     * @param bs
     * @throws IOException
     */
    public static void writeBytesToFile(String spath,byte[] bs) throws IOException{
        OutputStream out = new FileOutputStream(spath);
        InputStream is = new ByteArrayInputStream(bs);
        byte[] buff = new byte[1024];
        int len = 0;
        while((len=is.read(buff))!=-1){
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
    }
    
}