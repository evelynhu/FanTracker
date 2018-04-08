package com.mii.android.ad.type;

import android.content.Context;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.mii.android.ad.IAdAction;

public class BannerAdView implements IAdAction {

    private static AdView mBannerAd;
    private Context mContext;
    private String mPlacementId;
    private static LinearLayout mBannerAdContainer;

    public BannerAdView(Context context, String placementId, LinearLayout container) {
        this.mContext = context;
        this.mPlacementId = placementId;
        mBannerAdContainer = container;
    }

    public void create() {
        mBannerAd = new AdView(mContext, mPlacementId, AdSize.BANNER_HEIGHT_50);
        mBannerAdContainer.addView(mBannerAd);
        mBannerAd.loadAd();
    }

    @Override
    public void destroy() {
        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }

}