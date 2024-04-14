package com.hxt5gh.mergevideojava;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

public class FileCreationUtils {

    public static String createVideoFile(Context context) {
        String filePath = "";
        String filePrefix = "merged";
        String fileExtn = ".mp4";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues valuesvideos = new ContentValues();
            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Folder");
            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix + System.currentTimeMillis());
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix + System.currentTimeMillis() + fileExtn);
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);

            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    filePath = cursor.getString(index);
                }
                cursor.close();
            } else {
                filePath = uri.getPath();
            }

        } else {
            File dest = new File(new File("app_folder"), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists()) {
                fileNo++;
                dest = new File(new File("app_folder"), filePrefix + fileNo + fileExtn);
            }

            filePath = dest.getAbsolutePath();
        }

        return filePath;
    }


}
