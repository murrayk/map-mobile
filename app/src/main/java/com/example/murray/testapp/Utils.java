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

    public void copyOfflineMap(String filename, AssetManager assetManager, String packageName, ListRoutesFragment.UpdateProgress progressBarHandler){
        offlineMap = copyFileFromAssets(filename, assetManager, packageName, progressBarHandler);
    }

    public File copyFileFromAssets(String filename, AssetManager assetManager, String packageName, ListRoutesFragment.UpdateProgress progressBarHandler) {

        String baseDir = Environment.getExternalStorageDirectory().getPath() + "/" + packageName;

        File baseDirectory = new File(baseDir);
        if(!baseDirectory.exists()){
            baseDirectory.mkdirs();
        }


        File fileLocationOnSD = new File(baseDir, filename);
        if(fileLocationOnSD.exists()){
            return fileLocationOnSD;
        }


        long size = 20816896;


        InputStream in;
        OutputStream out;
        try {

            Log.i("tag", "copyFile() " + filename);
            in = assetManager.open(filename);

            out = new FileOutputStream(fileLocationOnSD);

            byte[] buffer = new byte[1024 * 32];
            int read;
            int amountRead =0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                amountRead += read;
                int percentage = Math.round((float)amountRead/size * 100);
                if(progressBarHandler != null){
                    progressBarHandler.updateProgressBar(percentage);
                }
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+filename);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

        return fileLocationOnSD;

    }
}
