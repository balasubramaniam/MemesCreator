package com.app.memecreator.tamil_new.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

import com.app.memecreator.tamil_new.BuildConfig;
import com.app.memecreator.tamil_new.R;
import com.app.memecreator.tamil_new.util.Helpers;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERM = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash__activity);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        requestStoragePermission();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "This app needs the Storage permission, because it will save memes to your Pictures folder.\n" +
                                "No need to worry, nothing gets sent to the web.", Snackbar.LENGTH_LONG).show();
            }

            // If we already learned why this is needed, request the perm from user
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERM);
            return;
        }

        // Older device API, or already granted
        startMemeCreator(false);
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perm, int[] grantResults) {
        boolean somethingGranted = grantResults.length > 0;
        switch (req) {
            case REQUEST_STORAGE_PERM:
                if (somethingGranted && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMemeCreator(true);
                    return;
                }
                break;
        }
        Toast.makeText(this, "Cannot start Meme-Creator without Storage Permission.", Toast.LENGTH_LONG).show();
    }

    private void startMemeCreator(boolean skipDelay) {
        // Create MemeCreator directories
        int delay = (skipDelay || BuildConfig.DEBUG) ? 1000 : getResources().getInteger(R.integer.splash_delay);
        File dirMemeTastic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        new File(dirMemeTastic, getString(R.string.dot_thumbnails)).mkdirs();


        // Start activity and exit splash
        // Skip if possible
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helpers.animateToActivity(SplashActivity.this, MainActivity.class, 0, true);
            }
        }, delay);
    }
}
