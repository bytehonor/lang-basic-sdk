package com.bytehonor.sdk.lang.bytehonor.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytehonor.sdk.lang.bytehonor.string.StringObject;

/**
 * @author lijianqiang
 *
 */
public class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    public static String getFileSubfixNoDot(String url) {
        String subfix = getFileSubfixWithDot(url);
        if (StringObject.isEmpty(subfix)) {
            return subfix;
        }

        return subfix.substring(1, subfix.length());
    }

    public static String getFileSubfixWithDot(String url) {
        Objects.requireNonNull(url, "url");
        int at = url.indexOf('?');
        if (at > 1) {
            url = url.substring(0, at);
        }
        at = url.lastIndexOf('.');
        if (at < 0) {
            return "";
        }
        if (url.length() - at > 7) {
            return "";
        }
        return url.substring(at).toLowerCase();
    }

    /**
     * 获得指定文件的byte数组
     * 
     * @param filePath 文件绝对路径
     * @return
     */
    public static byte[] file2Byte(String filePath) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            bos = new ByteArrayOutputStream((int) file.length());
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            LOG.error("file2Byte error", e);
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                LOG.error("finally", e);
            }
        }
    }

    /**
     * 根据byte数组，生成文件
     * 
     * @param bfile    文件数组
     * @param fileDir  文件存放路径
     * @param fileName 文件名称
     */
    public static File byte2File(byte[] bfile, String fileDir, String fileName) {
        if (StringObject.isEmpty(fileDir) || StringObject.isEmpty(fileName)) {
            throw new RuntimeException("byte2File param is invalid");
        }
        isExistDir(fileDir);// 判断文件目录是否存在
        String filePath = filePath(fileDir, fileName);
        File file = new File(filePath);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            LOG.error("byte2File error", e);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                LOG.error("byte2File finally error", e);
            }
        }
        return file;
    }

    /**
     * 判断多级路径是否存在，不存在就创建
     * 
     * @param filePath 支持带文件名的Path：如：D:\news\2014\12\abc.text，和不带文件名的Path：如：D:\news\2014\12
     */
    public static void isExistDir(String filePath) {
        String paths[] = { "" };
        // 切割路径
        try {
            String tempPath = new File(filePath).getCanonicalPath();// File对象转换为标准路径并进行切割，有两种windows和linux
            paths = tempPath.split("\\\\");// windows
            if (paths.length == 1) {
                paths = tempPath.split("/");
            } // linux
        } catch (IOException e) {
            LOG.error("切割路径错误, filePath:{}, error:{}", filePath, e.getMessage());
        }
        // 判断是否有后缀
        boolean hasType = false;
        if (paths.length > 0) {
            String tempPath = paths[paths.length - 1];
            if (tempPath.length() > 0) {
                if (tempPath.indexOf(".") > 0) {
                    hasType = true;
                }
            }
        }
        // 创建文件夹
        String dir = paths[0];
        for (int i = 0; i < paths.length - (hasType ? 2 : 1); i++) {// 注意此处循环的长度，有后缀的就是文件路径，没有则文件夹路径
            try {
                dir = dir + "/" + paths[i + 1];// 采用linux下的标准写法进行拼接，由于windows可以识别这样的路径，所以这里采用警容的写法
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                    LOG.info("成功创建目录：" + dirFile.getCanonicalFile());
                }
            } catch (Exception e) {
                LOG.error("文件夹创建发生异常", e);
            }
        }
    }

    private static String filePath(String fileDir, String fileName) {
        String filePath = fileDir;
        if (filePath.endsWith("/") == false && fileName.startsWith("/") == false) {
            filePath += "/";
        }
        filePath += fileName;
        return filePath;
    }

    public static File download(String fileUrl, String fileDir, String fileName) {
        if (StringObject.isEmpty(fileUrl) || StringObject.isEmpty(fileDir) || StringObject.isEmpty(fileName)) {
            throw new RuntimeException("download file param is invalid");
        }
        isExistDir(fileDir);
        LOG.debug("download fileDir:{}, fileName:{}", fileDir, fileName);
        String filePath = filePath(fileDir, fileName);
        File file = new File(filePath);
        // 获取连接
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8 * 1000);
            // 设置请求头
            // connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            // 获取输入流
            in = connection.getInputStream();
            out = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        } catch (Exception e) {
            LOG.error("byte2File error", e);
            throw new RuntimeException("下载文件失败");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                LOG.error("byte2File finally error", e);
            }
        }

        return file;
    }
}
