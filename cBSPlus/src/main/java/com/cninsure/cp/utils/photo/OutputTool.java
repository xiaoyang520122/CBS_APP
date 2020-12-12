package com.cninsure.cp.utils.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.cninsure.cp.utils.UriUtils;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Sailist on 2018/8/22/0022.
 * 封装获取相机存储路径和隐式意图启动相机的代
 */

public class OutputTool {
    private static OutputTool instance;
    private OutputTool(){}
    public static OutputTool  getInstance()
    {
        if(instance == null)
        {
            instance = new OutputTool();
        } 
        return instance;
    }

    private String mCurrentPhotoPath;

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public void takeCamera(Activity activity, int num) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getApplicationContext().getPackageManager())
                != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile(activity);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, UriUtils.getUri(activity, photoFile));
            }
        }

        Log.i("void", "takeCamera: startActivity");
        activity.startActivityForResult(takePictureIntent, num);//跳转界面传回拍照所得数据
        Log.i("void", "takeCamera: startActivityAfter");
    }
    @SuppressLint("NewApi")
	private File createImageFile(Activity activity) {
        File[] files =activity.getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_DCIM);
        for(File file:files){
            Log.e("main", String.valueOf(file));
        }

        File storageDir = activity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.i(TAG, "createImageFile: st" + storageDir);
        File image = null;
        try {
            image = File.createTempFile(
                    generateFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            mCurrentPhotoPath = image.getAbsolutePath();
            Log.i("void", "createImageFile: "+mCurrentPhotoPath);
        } catch (IOException e) {
            Log.i(TAG, "createImageFile: errrrr");
            e.printStackTrace();
        }


        return image;
    }

    public static String generateFileName() {

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        return imageFileName;
    }


}

