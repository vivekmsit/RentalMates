package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vivek.rentalmates.others.AppData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Background Async task to load user profile picture from url
 */
public class LoadProfileImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "LoadProfileImage_Debug";

    private Context context;
    private String emailId;
    private AppData appData;

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
            String path = context.getApplicationContext().getFilesDir().getPath() + "/" + this.emailId + ".png";
            try {
                OutputStream outputStream = new FileOutputStream(path);
                Bitmap newResult = Bitmap.createScaledBitmap(result, 200, 200, true);
                newResult.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                this.appData.getProfilePicturesPath().put(this.emailId, path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Profile Picture could not be retrieved for " + this.emailId);
        }
    }
}
