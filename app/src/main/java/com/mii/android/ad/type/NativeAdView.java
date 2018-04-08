package com.mii.android.ad.type;

import android.content.Context;

import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.mii.android.ad.IAdAction;

import com.mii.android.ui.page.PageAdapter;

import java.util.List;

public class NativeAdView implements IAdAction {

    private static final String TAG = NativeAdView.class.getName();
    private static NativeAd mNativeAd;
    private Context mContext;
    private String mPlacementId;
    private int mAdIndex = 4;
    private PageAdapter mAdapter;
    private List<Object> mRecyclerViewItems;

    public NativeAdView(Context context, String placemoneId, List<Object> recyclerViewItems, PageAdapter adapter) {
        this.mContext = context;
        this.mPlacementId = placemoneId;
        this.mAdapter = adapter;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public void create() {
        mNativeAd = new NativeAd(this.mContext, this.mPlacementId);
        mNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });
        mNativeAd = new NativeAd(mContext, mPlacementId);
        mNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "[onError]" + adError.toString());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "[onAdLoaded] insert : " + mAdIndex + ", size : " + mRecyclerViewItems.size() + ", keys size : " + mAdapter.getKeySize());
                if (mRecyclerViewItems.size() > mAdIndex) {
                    mRecyclerViewItems.add(mAdIndex, ad);
                    mAdapter.insertKey(mAdIndex, String.valueOf(ad.hashCode()));
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "[onAdLoaded] " + mAdIndex + " - " + ad.toString());
                    mAdIndex = (mAdIndex + 4) % mRecyclerViewItems.size();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "[onLoggingImpression]" + ad.toString());
            }
        });
        mNativeAd.loadAd();
    }

    @Override
    public void destroy() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
    }
}