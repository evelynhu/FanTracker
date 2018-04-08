package com.mii.android.ad.type;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.InstreamVideoAdListener;
import com.facebook.ads.InstreamVideoAdView;
import com.mii.android.ad.IAdAction;
import com.mii.android.ui.page.PageFragment;

public class InStreamAdView implements IAdAction {

    private static final String TAG = PageFragment.class.getName();
    private static InstreamVideoAdView mInstreamAd;
    private Context mContext;
    private String mPlacementId;
    private static LinearLayout mBannerAdContainer;

    public InStreamAdView(Context context, String placemoneId, LinearLayout container) {
        this.mContext = context;
        this.mPlacementId = placemoneId;
        mBannerAdContainer = container;
    }

    private int pxToDP(int px) {
        return (int)(px / mContext.getResources().getDisplayMetrics().density);
    }

    public void create() {
        // Get the Ad Container
        mInstreamAd = new InstreamVideoAdView(
                mContext,
                mPlacementId,
                new AdSize(
                        pxToDP(mBannerAdContainer.getMeasuredWidth()),
                        pxToDP(mBannerAdContainer.getMeasuredHeight())
                )
        );
        // set ad listener to handle events
        mInstreamAd.setAdListener(new InstreamVideoAdListener() {
            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "onLoggingImpression : " + ad.toString());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // we have an ad so let's show it
                mBannerAdContainer.addView(mInstreamAd);
                mInstreamAd.show();
            }


            @Override
            public void onAdVideoComplete(Ad ad) {
                Log.d(TAG, "ad video complete : " + ad.toString());
                mBannerAdContainer.setVisibility(View.GONE);
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "onAdClicked : " + ad.toString());
            }
        });
        mInstreamAd.loadAd();
    }

    @Override
    public void destroy() {
        if (mInstreamAd != null) {
            mInstreamAd.destroy();
        }
    }
}