package com.mii.android.ad.type;

import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.mii.android.ad.IAdAction;
import com.mii.android.ui.page.PageFragment;

public class InterstitialAdView implements IAdAction {

    private static final String TAG = PageFragment.class.getName();
    private static InterstitialAd mInterstitialAd;
    private Context mContext;
    private String mPlacementId;

    public InterstitialAdView(Context context, String placemoneId) {
        this.mContext = context;
        this.mPlacementId = placemoneId;
    }

    public void create() {
        mInterstitialAd = new InterstitialAd(mContext, mPlacementId);
        // Set listeners for the Interstitial Ad
        mInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        mInterstitialAd.loadAd();
    }

    @Override
    public void destroy() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
        }
    }
}