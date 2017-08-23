package com.app.memecreator.tamil_new;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.app.memecreator.tamil_new.data.MemeCategory;
import com.app.memecreator.tamil_new.data.MemeFont;
import com.app.memecreator.tamil_new.data.MemeLibConfig;
import com.app.memecreator.tamil_new.util.AppSettings;
import com.app.memecreator.tamil_new.util.Helpers;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;


/**
 * The apps application object
 */
public class App extends Application {
    public AppSettings settings;
    List<MemeCategory> memeCategories;
    List<MemeFont> fonts;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        settings = new AppSettings(this);
        loadFonts();
        loadMemeNames();
        MobileAds.initialize(this, "ca-app-pub-8206768236452234~3148632908");
        sAnalytics = GoogleAnalytics.getInstance(this);

    }

    public void loadFonts() {
        String FONT_FOLDER = MemeLibConfig.getPath(MemeLibConfig.Assets.FONTS, false);
        try {
            String[] fontFilenames = getAssets().list(FONT_FOLDER);
            FONT_FOLDER = MemeLibConfig.getPath(FONT_FOLDER, true);
            fonts = new ArrayList<>();

            for (int i = 0; i < fontFilenames.length; i++) {
                Typeface tf = Typeface.createFromAsset(getResources().getAssets(), FONT_FOLDER + fontFilenames[i]);
                fonts.add(new MemeFont(FONT_FOLDER + fontFilenames[i], tf));
            }
        } catch (IOException e) {
            log("Could not load fonts");
            fonts = new ArrayList<>();
        }
    }

    public void loadMemeNames() {
        String IMAGE_FOLDER = MemeLibConfig.getPath(MemeLibConfig.Assets.MEMES, false);
        try {
            String[] memeCategories = getAssets().list(IMAGE_FOLDER);
            IMAGE_FOLDER = MemeLibConfig.getPath(IMAGE_FOLDER, true);
            this.memeCategories = new ArrayList<MemeCategory>();

            for (String memeCat : memeCategories) {
                this.memeCategories.add(new MemeCategory(memeCat, getAssets().list(IMAGE_FOLDER + memeCat)));
            }
        } catch (IOException e) {
            log("Could not load images");
            memeCategories = new ArrayList<MemeCategory>();
        }
    }

    public List<MemeFont> getFonts() {
        return this.fonts;
    }

    // Get meme category object (parameter = foldername in assets)
    public MemeCategory getMemeCategory(String category) {
        for (MemeCategory cat : memeCategories) {
            if (cat.getCategoryName().equalsIgnoreCase(category))
                return cat;
        }
        return null;
    }

    public void shareBitmapToOtherApp(Bitmap bitmap, Activity activity) {
        File imageFile = Helpers.saveBitmapToFile(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.cached_picture_filename), bitmap);
        if (imageFile != null) {
            Uri imageUri = Uri.fromFile(imageFile);
            if (imageUri != null) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                Intent intentChooser = Intent.createChooser(sharingIntent, "Share Meme Using");
                intentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentChooser);
                /*Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setDataAndType(imageUri, getContentResolver().getType(imageUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                activity.startActivity(Intent.createChooser(shareIntent, getString(R.string.main__share_meme_prompt)));*/
            }
        }
    }

    public static void log(String text) {
        if (BuildConfig.DEBUG) {
            Log.d("MemeTastic", text);
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
