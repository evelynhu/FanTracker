package com.mii.android.ad.type;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.ads.RewardedVideoAd;
import com.mii.android.ad.IAdAction;
import com.mii.android.ui.page.PageFragment;

public class RewardedVideoAdView implements IAdAction {

    private static final String TAG = PageFragment.class.getName();
    private static RewardedVideoAd mRewardedVideoAd;
    private Context mContext;
    private String mPlacementId;

    public RewardedVideoAdView(Context context, String placemoneId) {
        this.mContext = context;
        this.mPlacementId = placemoneId;
    }

    public void create() {
        if (mRewardedVideoAd == null) {
            mRewardedVideoAd = new RewardedVideoAd(mContext, mPlacementId);
            mRewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    // Rewarded video ad failed to load
                    Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Rewarded video ad is loaded and ready to be displayed
                    Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                    mRewardedVideoAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Rewarded video ad clicked
                    Log.d(TAG, "Rewarded video ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Rewarded Video ad impression - the event will fire when the
                    // video starts playing
                    Log.d(TAG, "Rewarded video ad impression logged!");
                }

                @Override
                public void onRewardedVideoCompleted() {
                    // Rewarded Video View Complete - the video has been played to the end.
                    // You can use this event to initialize your reward
                    Log.d(TAG, "Rewarded video completed!");
                    // Call method to give reward
                    // giveReward();
                }

                @Override
                public void onRewardedVideoClosed() {
                    // The Rewarded Video ad was closed - this can occur during the video
                    // by closing the app, or closing the end card.
                    Log.d(TAG, "Rewarded video ad closed!");
                }
            });
            mRewardedVideoAd.loadAd();
        }
    }

    @Override
    public void destroy() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy();
        }
    }
}