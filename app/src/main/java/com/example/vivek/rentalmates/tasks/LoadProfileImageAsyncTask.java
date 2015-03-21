package com.example.vivek.rentalmates.tasks;

/**
 * Created by vivek on 3/21/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.others.AppData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Background Async task to load user profile picture from url
 * */
public class LoadProfileImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "LoadProfileImage_Debug";

    Context context;
    String emailId;
    AppData appData;

    public LoadProfileImageAsyncTask(Context context, String emailId) {
        this.context = context;
        this.emailId = emailId;
        this.appData = AppData.getInstance();
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            Log.d(TAG, "ProfilePicture retrieved successfully for " + this.emailId);
            String path = context.getApplicationContext().getFilesDir().getPath() + "/" + this.emailId;
            File file = new File(path);
            try {
                OutputStream outputStream = new FileOutputStream(file);
                result.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
                this.appData.getProfilePicturesPath().put(this.emailId, path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Profile Picture could not be retrieved for " + this.emailId);
        }
    }
}
