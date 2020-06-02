package com.oyf.codecollection.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;

/**
 * @创建者 oyf
 * @创建时间 2020/4/28 15:49
 * @描述
 **/
public class HuaWeiBitmapUtils {
    public static Bitmap yuv2Bitmap(byte[] data, int width, int height) {
        Bitmap bitmap = convertToBitmap(width, height, data);
        return bitmap;
    }

    private static Bitmap convertToBitmap(int width, int height, byte[] data) {
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
    }

}
