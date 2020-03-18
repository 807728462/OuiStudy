package com.oyf.codecollection;

import java.io.File;

/**
 * @创建者 oyf
 * @创建时间 2020/3/12 15:28
 * @描述
 **/
public class TestMainUpdateName {
    public static void main(String[] args) {
        String filePath = "D:\\company\\tcode\\TAspectJ\\app\\src\\main\\res";
        String oldFileName = "组 208.png";
        String newFileName = "bg_guide_1_4.png";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith("mipmap")) {
                File[] items = files[i].listFiles();
                for (int j = 0; j < items.length; j++) {
                    File path = items[j];
                    if (path.getName().equals(oldFileName)) {
                        FixFileName(path.getAbsolutePath() + "\\" + oldFileName, newFileName);
                    }
                }
            }
        }
        System.out.println("修改完成");
    }

    private static String FixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return null;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return newFilePath;
    }
}
