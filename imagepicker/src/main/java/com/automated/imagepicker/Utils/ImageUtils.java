package com.automated.imagepicker.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageUtils
{
    public static String convertbase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
    }


    public static Bitmap converfiletobitmap(File file){
        String filePath = file.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }

}
