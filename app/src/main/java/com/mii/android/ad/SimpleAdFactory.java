package com.mii.android.ad;

import android.content.Context;
import android.widget.LinearLayout;

import com.mii.android.ad.type.*;
import com.mii.android.ui.page.PageAdapter;
import com.mii.android.util.AdType;

import java.util.List;

public class SimpleAdFactory {
    private Context mContext;
    private String mPlacementId;
    private LinearLayout mContainer;
    private PageAdapter mAdapter;
    private List<Object> mRecyclerViewItems;

    public SimpleAdFactory(Context context, String placementId, LinearLayout container, List<Object> recyclerViewItems, PageAdapter adapter) {
        this.mContext = context;
        this.mPlacementId = placementId;
        this.mContainer = container;
        this.mRecyclerViewItems = recyclerViewItems;
        this.mAdapter = adapter;
    }

    public IAdAction createAd(int adType) {
        if (adType == AdType.NATIVE)
            return new NativeAdView(mContext, mPlacementId,this.mRecyclerViewItems, this.mAdapter);
        if (adType == AdType.BANNER)
            return new BannerAdView(mContext, mPlacementId, mContainer);
        if (adType == AdType.INTERSTITIAL)
            return new InterstitialAdView(mContext, mPlacementId);
        if (adType == AdType.INTREAM)
            return new IntreamAdView(mContext, mPlacementId, mContainer);
        if (adType == AdType.REWARD_VIDEO)
            return new RewardedVideoAdView(mContext, mPlacementId);
        else
            return null;
    }
}