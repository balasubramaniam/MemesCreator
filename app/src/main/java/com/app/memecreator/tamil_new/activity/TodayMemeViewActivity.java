package com.app.memecreator.tamil_new.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.memecreator.tamil_new.App;
import com.app.memecreator.tamil_new.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity for creating memes
 */
public class TodayMemeViewActivity extends AppCompatActivity{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_preview)
    ImageView imageEditView;

    @BindView(R.id.share_fab)
    FloatingActionButton shareFAB;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    App app;

    private Bitmap mBitmap;
    private InterstitialAd mInterstitialAd;
    private boolean doubleBackToExitPressedOnce;

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memetoday__activity);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Bind Ui
        ButterKnife.bind(this);
        app = (App) getApplication();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8206768236452234/2280564904");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        // Set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra("url")){
            String filepath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name)).getAbsolutePath();

            new GetBitmapAsyncTask(filepath).execute(getIntent().getExtras().getString("url"));
            /*Picasso.with(this)
                    .load(getIntent().getExtras().getString("url"))
                    .into(imageEditView, new Callback() {
                        @Override
                        public void onSuccess() {
                            shareFAB.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });*/


        }

        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBitmap != null){
                    app.shareBitmapToOtherApp(mBitmap, TodayMemeViewActivity.this);

                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Snackbar.make(findViewById(android.R.id.content), R.string.creator__press_back_again_to_exit, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });

    }

    public class GetBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String mFilePathToSave;

        public GetBitmapAsyncTask(String path) {
            mFilePathToSave = path;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.GONE);
            mBitmap = bitmap;
            if (bitmap != null) {
                File file = new File(mFilePathToSave,"today.jpg");
                try {
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                    ostream.flush();
                    ostream.close();
                    imageEditView.setImageBitmap(bitmap);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        doubleBackToExitPressedOnce = true;

    }


}
