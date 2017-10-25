package com.shrek.klib.file;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.shrek.klib.colligate.AndroidVersionCheckUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    /**
     * 得到一个可用的缓存目录(如果外部可用使用外部,否则内部)。
     *
     * @param context 上下文信息
     * @return 返回目录名字
     */
    public static File getDiskCacheDir(Context context) {
        // 检查是否安装或存储媒体是内置的,如果是这样,试着使用
        // 外部缓存 目录
        // 否则使用内部缓存 目录
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(
                context).getPath()
                : context.getCacheDir().getPath();

        return new File(cachePath + File.separator);
    }

    /**
     * 检查如果外部存储器是内置的或是可移动的。
     *
     * @return 如果外部存储是可移动的(就像一个SD卡)返回为 true,否则false。
     */
    public static boolean isExternalStorageRemovable() {
        if (AndroidVersionCheckUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }


    /**
     * 获得外部应用程序缓存目录
     *
     * @param context 上下文信息
     * @return 外部缓存目录
     */
    public static File getExternalCacheDir(Context context) {
        if (AndroidVersionCheckUtils.hasFroyo()) {
            // return context.getExternalCacheDir();
            return Environment.getExternalStorageDirectory();
        }
        final String cacheDir = "/data/data/" + context.getPackageName()
                + "/cache";
        return new File(cacheDir);
    }

    /**
     * 智能的创建文件夹
     * 外置sdcard
     *
     * @return
     */
    public static File wisdomMakeDir(Context context) {
        File makeDir = new File("");
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            makeDir = context.getExternalCacheDir();
        } else {
            makeDir = context.getCacheDir();
        }
        if (!makeDir.exists()) {

        }
        return makeDir;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        return file.delete();
    }


    /**
     * 拷贝文件
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File src, File dest) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dest).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }


    public static void downLoad(Context context, String downloadUrl, String fileName) {
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("/download/", fileName);
        //获取下载管理器
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }
}
