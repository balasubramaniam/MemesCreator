package com.app.memecreator.tamil_new.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.memecreator.tamil_new.R;
import com.app.memecreator.tamil_new.util.Helpers;
import com.app.memecreator.tamil_new.util.SimpleMarkdownParser;

public class InfoActivity extends AppCompatActivity {
    //####################
    //##  Ui Binding
    //####################
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.info__activity__text_app_version)
    TextView textAppVersion;

    @BindView(R.id.info__activity__text_maintainers)
    TextView textMaintainers;

    @BindView(R.id.info__activity__text_contributors)
    TextView textContributors;

    //####################
    //##  Methods
    //####################
    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info__activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textMaintainers.setText(new SpannableString(Html.fromHtml(
                Helpers.loadMarkdownFromRawForTextView(this, R.raw.maintainers, ""))));
        textMaintainers.setMovementMethod(LinkMovementMethod.getInstance());

        textContributors.setText(new SpannableString(Html.fromHtml(
                Helpers.loadMarkdownFromRawForTextView(this, R.raw.contributors, "* ")
        )));
        textContributors.setMovementMethod(LinkMovementMethod.getInstance());


        // App version
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            textAppVersion.setText(getString(R.string.app_version_v, info.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.info__activity__text_app_version)
    public void onVersionClicked(View v) {
        Helpers.openWebpageWithExternalBrowser(this, getString(R.string.app_www_source));
    }

    @OnClick({R.id.info__activity__text_app_version, R.id.info__activity__button_third_party_licenses, R.id.info__activity__button_gplv3_license})
    public void onButtonClicked(View v) {
        Context context = v.getContext();
        switch (v.getId()) {
            case R.id.info__activity__text_app_version: {
                Helpers.openWebpageWithExternalBrowser(context, getString(R.string.app_www_source));
                break;
            }
            case R.id.info__activity__button_gplv3_license: {
                Helpers.showDialogWithHtmlTextView(this, Helpers.loadMarkdownFromRawForTextView(this, R.raw.license, ""), R.string.info__licenses);
                break;
            }
            case R.id.info__activity__button_third_party_licenses: {
                try {
                    Helpers.showDialogWithHtmlTextView(this, new SimpleMarkdownParser().parse(
                            getResources().openRawResource(R.raw.licenses_3rd_party),
                            SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, "").getHtml(),
                            R.string.info__licenses);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
