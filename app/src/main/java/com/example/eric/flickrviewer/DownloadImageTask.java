package com.example.eric.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];

        URL url = null;
        try {
            url = new URL(urldisplay);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Cache the images
        if (connection != null) {
            connection.setUseCaches(true);
        }
        
        Object response = null;
        Bitmap bitmap = null;
        try {
            response = BitmapFactory.decodeStream((InputStream) connection.getContent());
            bitmap = (Bitmap)response;

            if(bitmap != null) {
                float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();

                int height = Math.round(MainActivity.width / aspectRatio);

                bitmap = Bitmap.createScaledBitmap(bitmap, MainActivity.width, height, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}