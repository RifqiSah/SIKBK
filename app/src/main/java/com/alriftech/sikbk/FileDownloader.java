package com.alriftech.sikbk;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDownloader {
    private static final int  MEGABYTE = 1024 * 1024;
    private static String fileName;
    private static String folder;

    public static void downloadFile(String fileUrl){
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            //Extract file name from URL
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());
            fileName = timestamp + "_" + fileName;

            //External directory path to save file
            folder = Environment.getExternalStorageDirectory() + File.separator + "SIKBK/";

            //Create androiddeft folder if it does not exist
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(folder + fileName);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while((bufferLength = inputStream.read(buffer))>0 ){
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}