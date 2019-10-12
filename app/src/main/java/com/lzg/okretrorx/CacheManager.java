package com.lzg.okretrorx;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.lzg.okretrorx.http.MD5;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.Buffer;
import okio.Sink;
import okio.Source;

/**
 *  * Created by 智光 on 2019/10/11 15:34
 *  
 */
public class CacheManager {

    private Context mContext;
    private static CacheManager instance;
    private static DiskLruCache diskLruCache;
    private File fileCacheDir;

    public CacheManager(Context mContext) {
        this.mContext = mContext;
        fileCacheDir = getCacheFile(mContext, "gzlCache");
        diskLruCache = DiskLruCache.Companion.create(FileSystem.SYSTEM, fileCacheDir, getVersionName(mContext), 1, 10 * 1024 * 1024);
    }

    public static CacheManager getInstance(Context mContext) {
        if (instance == null) {
            instance = new CacheManager(mContext);
        }
        return instance;
    }


    /**
     * 写入文件操作，
     * 通过DiskLruCache中的方法edit获取对应key的Editor对象，
     * 通过这个Editor对象获取到输出流对象Sink，该类实现了OutputStream，可当做OutputStream来看，
     * try{
     * DiskLruCache.Snapshot snapShot = null;
     * // 生成图片URL对应的key
     * final String key = hashKeyForDisk(imageUrl);
     * // 查找key对应的缓存
     * snapShot = mDiskLruCache.get(key);
     * if (snapShot == null) {
     * // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
     * DiskLruCache.Editor editor = mDiskLruCache.edit(key);
     * if (editor != null) {
     * Sink sink = editor.newSink(0);
     * boolean result = downloadImage(strings[0],sink);
     * if(result){
     * editor.commit();
     * } else {
     * editor.abort();
     * }
     * }
     * // 缓存被写入后，再次查找key对应的缓存
     * snapShot = mDiskLruCache.get(key);
     * }
     * <p>
     * 原文链接：https://blog.csdn.net/seevc/article/details/79025109
     */
    public void addCache(String body, String apiurl) {
        DiskLruCache.Snapshot snapShot = null;
        //根据url生成对应的key
        String key = MD5.md5Hex(apiurl);
        //查找key对应的缓存
        try {
            snapShot = diskLruCache.get(key);

            if (snapShot == null) {
                DiskLruCache.Editor editor = diskLruCache.edit(key);
                if (editor != null) {
                    Sink sink = editor.newSink(0);
                    boolean result = writeToDisk(body, sink);
                    if (result) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                snapShot = diskLruCache.get(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapShot != null) {
                snapShot.close();
            }
        }
    }

    public synchronized String getCacheByUrl(String url) {
        String result = "";
        DiskLruCache.Snapshot snapShot = null;
        Source source = null;
        Buffer buffer = null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        String key = MD5.md5Hex(url);
        try {
            snapShot = diskLruCache.get(key);

            if (snapShot != null) {
                //获取资源的输出流,Source类似InputStream
                source = snapShot.getSource(0);

                buffer = new Buffer();
                //读取4*1024数据放入buffer中并返回读取到的数据的字节长度
                long ret = source.read(buffer, 4 * 1024);
                //判断文件是否读完
                while (ret != -1) {
                    ret = source.read(buffer, 4 * 1024);
                }
                source.close();
                //获取到buffer的inputStream对象
                inputStream = buffer.inputStream();
                //使用BitmapFactory解析inputStream并返回一个Bitma
                outputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, length);
                }
                result = outputStream.toString(StandardCharsets.UTF_8.name());
                inputStream.close();
                outputStream.close();
                Log.d(CacheManager.class.getName(), "获取到缓存数据" + result);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapShot != null) {
                snapShot.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        }
        return null;
    }

    /**
     * 写入到磁盘
     *
     * @param string
     * @param sink
     * @return
     */
    private static boolean writeToDisk(String string, Sink sink) {
        Log.d(CacheManager.class.getName(), "开始写入缓存");
        InputStream inputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            Buffer buffer = new Buffer();
            inputStream = new ByteArrayInputStream(string.getBytes());
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                buffer.write(fileReader, 0, read);
                sink.write(buffer, read);
                buffer.clear();
            }
            buffer.clear();
            buffer.close();

            inputStream.close();

            if (sink != null) {
                sink.flush();
                sink.close();
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }


    private static int getVersionName(Context context) {
        int versionName = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionCode;
        } catch (Exception e) {
            return versionName;
        }
        return versionName;
    }

    //获取Cache 存储目录
    private static File getCacheFile(Context context, String uniqueName) {

        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = Objects.requireNonNull(context.getExternalCacheDir()).getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = new File(cachePath + "/" + uniqueName);
        if (!file.exists()) file.mkdirs();
        return file;

    }
}
