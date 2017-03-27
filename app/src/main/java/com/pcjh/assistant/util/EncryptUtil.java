package com.pcjh.assistant.util;

import android.os.Environment;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by szhua on 2016/11/23.
 * gZip 加解密工具类 ;
 */
public class EncryptUtil {

    /**
     * 缓冲区的大小 ;
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * 将文件储存在手机的根目录下————Test
     * @param bytes
     * @param fileName
     */
    public static void createFileInSdcard(byte[] bytes,String fileName){
        File file =new File(Environment.getExternalStorageDirectory(),fileName);
        FileOutputStream fileOutputStream =null ;
        BufferedOutputStream bufferedOutputStream =null ;
        if(file.exists()){
            file.delete();
        }
        try {
            //确保文件的存在性，创建一个新的文件;
            file.createNewFile() ;
            fileOutputStream =new FileOutputStream(file) ;
            bufferedOutputStream =new BufferedOutputStream(fileOutputStream) ;
            bufferedOutputStream.write(bytes);
            //缓冲区的清除  ;
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fileOutputStream != null){
                    fileOutputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * GZIP 加密
     *
     * @param str
     * @return
     */
    public static String encryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            // gzip压缩
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(str.getBytes());
            gzip.close();

            byte[] encode = baos.toByteArray();

            baos.flush();
            baos.close();
            /**
             * 这里返回的String要经过Base64的加密;不然解密的话不会成功;
             */
            return new String(Base64.encode(encode, Base64.DEFAULT), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GZIP 解密
     * @param str
     * @return
     */
    public static  String decryptGZIP(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byte[] decode = str.getBytes();
            byte [] base64Decode = Base64.decode(decode,  Base64.DEFAULT) ;
            //gzip 解压缩
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Decode);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len=gzip.read(buf, 0, BUFFER_SIZE))!=-1){
                baos.write(buf, 0, len);
            }
            gzip.close();
            baos.flush();
            decode = baos.toByteArray();
            baos.close();
            return new String(decode);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
