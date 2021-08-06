package com.lann.openpark.util;//目录封装类

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件文件夹工具类
 *
 * @Author songqiang
 * @Description
 * @Date 2020/9/10 10:20
 **/
public class FileUtil {


    public static void main(String[] args) {
        getAllFolderNames("C:\\apache-tomcat-7.0.96\\webapps\\FileUpload\\" + "parkImgs");
    }

    /**
     * 遍历文件
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:34
     **/
    public static void getFiles(String path) {
        File file = new File(path);
        // 如果这个路径是文件夹
        if (file.isDirectory()) {
            // 获取路径下的所有文件
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 如果还是文件夹 递归获取里面的文件 文件夹
                if (files[i].isDirectory()) {
                    System.out.println("目录：" + files[i].getPath());
                    getFiles(files[i].getPath());
                } else {
                    System.out.println("文件：" + files[i].getPath());
                }

            }
        } else {
            System.out.println("文件：" + file.getPath());
        }
    }

    /**
     * 获取文件夹下所有文件夹的名字
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:34
     **/
    public static List<String> getAllFolderNames(String path) {
        List<String> list = new ArrayList();
        File file = new File(path);
        if (file.isDirectory()) {
            // 获取路径下的所有文件
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 如果还是文件夹 递归获取里面的文件 文件夹
                if (files[i].isDirectory()) {
                    // System.out.println("目录：" + files[i].getName());
                    list.add(files[i].getName());
                }

            }
        }
        // 如果这个路径是文件夹
        return list;
    }


    /**
     * 创建目录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:20
     **/
    public static boolean createDir(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return false;
    }

    /**
     * 创建多级目录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:20
     **/
    public static boolean createDirs(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return false;
    }

    /**
     * 重命名目录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:20
     **/
    public static boolean renameDir(String oldDir, String newDir) {
        File old = new File(oldDir);
        if (old.isDirectory()) {
            return old.renameTo(new File(newDir));
        }
        return false;
    }

    /**
     * 删除空目录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:20
     **/
    public static boolean deleteEmptyDir(String dirName) {
        File dir = new File(dirName);
        if (dir.isDirectory()) {
            return dir.delete();
        }
        return false;
    }

    /**
     * 递归删除目录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:20
     **/
    public static void deleteDirs(String dirName) {
        File dir = new File(dirName);
        File[] dirs = dir.listFiles();
        for (int i = 0; dirs != null && i < dirs.length; i++) {
            File f = dirs[i];
            //如果是文件直接删除
            if (f.isFile()) {
                f.delete();
            }
            //如果是目录继续遍历删除
            if (f.isDirectory()) {
                deleteDirs(f.getAbsolutePath());
            }
        }
        //删除本身
        dir.delete();
    }
}