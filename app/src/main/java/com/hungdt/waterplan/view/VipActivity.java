package com.hungdt.waterplan.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.hungdt.waterplan.Helper;
import com.hungdt.waterplan.KEY;
import com.hungdt.waterplan.MySetting;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.VipDetail;
import com.hungdt.waterplan.view.adater.DetailVIPAdapter;

import java.util.ArrayList;
import java.util.List;

public class VipActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private TextView txtStartNow;
    private ImageView imgBack;
    private BillingProcessor billingProcessor;
    private boolean readyToPurchase = false;

    private ViewPager vpVipDetails;
    private DetailVIPAdapter vipAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        Helper.setColorStatusBar(this, R.color.status_bar);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Banner
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        txtStartNow = findViewById(R.id.txtStartNow);
        vpVipDetails = findViewById(R.id.vpVipDetails);
        imgBack = findViewById(R.id.imgBack);

        billingProcessor = BillingProcessor.newBillingProcessor(this, getString(R.string.BASE64), this); // doesn't bind
        billingProcessor.initialize();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtStartNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readyToPurchase) {
                    billingProcessor.subscribe(VipActivity.this, getString(R.string.ID_SUBSCRIPTION));
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<VipDetail> vipDetails = new ArrayList<>();//Data trueyenf vao
        vipDetails.add(new VipDetail(R.drawable.ic_feature, "Get all feature", "Once & Forever", "Pay for once to use all feature.", "No more locked feature"));
        vipDetails.add(new VipDetail(R.drawable.ic_blockads, "", "No Ads", "Remove all ads that annoy you", "when experience this app."));
        vipDetails.add(new VipDetail(R.drawable.ic_getallfeature, "", "More Tracking Feature", "More activities of taking care", "of your plants."));
        vipDetails.add(new VipDetail(R.drawable.group_296, "Take care of", "Unlimited Plants", "Take care of all your plants", "with unlimited features."));
        vipAdapter = new DetailVIPAdapter(VipActivity.this, vipDetails);//setAdapter
        vpVipDetails.setAdapter(vipAdapter);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY.TYPE_RESULT, KEY.CREATE);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        try {
            Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
            MySetting.setSubscription(this, true);
            MySetting.putRemoveAds(this, true);
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Unable to process billing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
