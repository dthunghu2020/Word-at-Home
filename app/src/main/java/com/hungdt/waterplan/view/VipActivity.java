package com.hungdt.waterplan.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
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
        vipDetails.add(new VipDetail(R.drawable.ic_feature,"Get all feature","Once & Forever","Pay for once to use all feature.","No more locked feature"));
        vipDetails.add(new VipDetail(R.drawable.ic_blockads,"","No Ads","Remove all ads that annoy you","when experience this app."));
        vipDetails.add(new VipDetail(R.drawable.ic_getallfeature,"","More Tracking Feature","More activities of taking care","of your plants."));
        vipDetails.add(new VipDetail(R.drawable.group_296,"Take care of","Unlimited Plants","Take care of all your plants","with unlimited features."));
        vipAdapter = new DetailVIPAdapter(VipActivity.this, vipDetails);//setAdapter
        vpVipDetails.setAdapter(vipAdapter);

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
}
