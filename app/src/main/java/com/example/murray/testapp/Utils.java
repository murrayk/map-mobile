package com.example.murray.testapp;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by murray on 06/09/14.
 */
public class Utils {

    private File offlineMap;

    public File getOfflineMap(){
        if (offlineMap==null){
            //alert wait till db created
            throw new RuntimeException("alert wait till db created");
        }
        return  offlineMap;
    }

    private static Utils instance = new Utils();

    private Utils(){}

    public static Utils getInstance(){
        return instance;
    }

    public void copyOfflineMap(String filename, AssetManager assetManager, String packageName){
        offlineMap = copyFileFromAssets(filename,assetManager,packageName);
    }

    public File copyFileFromAssets(String filename, AssetManager assetManager, String packageName) {

        String baseDir = Environment.getExternalStorageDirectory().getPath() + "/" + packageName;

        File file = new File(baseDir + filename);
        if(file.exists()){
            return file;
        }




        InputStream in;
        OutputStream out;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() " + filename);
            in = assetManager.open(filename);

            newFileName = baseDir + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

        return file;

    }
}
