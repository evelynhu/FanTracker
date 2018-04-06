package com.mii.android.ui.page;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import com.mii.android.R;
import com.mii.android.model.Post;


/**
 * Created by mii on 2018/3/26.
 */

public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> postList;
    private List<String> keys = new ArrayList<>();
    private Context context;
    public static final int POST = 0;
    public static final int NATIVE_AD = 1;

    PageAdapter(Context context, List<Object> postList) {
        this.postList = postList;
        this.context = context;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public void insertKey(int position, String key) {
        if (position < keys.size()) {
            keys.add(position, key);
        }
    }

    public boolean containsKey(String key) {
        return keys.contains(key);
    }

    public void addKey(String key) {
        keys.add(key);
    }

    public void removeKey(String key) {
        if (keys.contains(key)) {
            keys.remove(key);
        }
    }

    public int getPostionFromKey(String key) {
        return keys.indexOf(key);
    }

    public int getKeySize() {
        return keys.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == POST) {
            View postItem = inflater.inflate(R.layout.item_post_list, parent, false);
            return new PostViewHolder(postItem);
        } else if (viewType == NATIVE_AD) {
            View nativeAdItem = inflater.inflate(R.layout.item_native_ad, parent, false);
            return new NativeAdViewHolder(nativeAdItem);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);

        if (itemType == POST) {
            PostViewHolder recipeViewHolder = (PostViewHolder) holder;
            Post post = (Post) postList.get(position);
            recipeViewHolder.bind(post);
        } else if (itemType == NATIVE_AD) {
            NativeAdViewHolder nativeAdViewHolder = (NativeAdViewHolder) holder;
            NativeAd nativeAd = (NativeAd) postList.get(position);

            ImageView adImage = nativeAdViewHolder.adImage;
            TextView tvAdTitle = nativeAdViewHolder.tvAdTitle;
            TextView tvAdBody = nativeAdViewHolder.tvAdBody;
            TextView btnCTA = nativeAdViewHolder.btnCTA;
            LinearLayout adChoicesContainer = nativeAdViewHolder.adChoicesContainer;
            MediaView mediaView = nativeAdViewHolder.mediaView;

            tvAdTitle.setText(nativeAd.getAdTitle());
            tvAdBody.setText(nativeAd.getAdBody());
            NativeAd.downloadAndDisplayImage(nativeAd.getAdIcon(), adImage);
            btnCTA.setText(nativeAd.getAdCallToAction());
            if (nativeAdViewHolder.adChoicesView == null) {
                AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) adChoicesView.getLayoutParams();
                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources()
                                .getDisplayMetrics());
                layoutParams.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
                adChoicesView.setLayoutParams(layoutParams);
                adChoicesContainer.addView(adChoicesView, 0);
                nativeAdViewHolder.setAdChoicesView(adChoicesView);
            }
            mediaView.setNativeAd(nativeAd);

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(adImage);
            clickableViews.add(btnCTA);
            clickableViews.add(mediaView);
            nativeAd.registerViewForInteraction(nativeAdViewHolder.container, clickableViews);
        }
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = postList.get(position);
        if (item instanceof Post) {
            return POST;
        } else if (item instanceof Ad) {
            return NATIVE_AD;
        } else {
            return -1;
        }
    }

    public Object getItem(int position) {
        if (getItemViewType(position) == POST) {
            return postList.get(position);
        } else if (getItemViewType(position) == NATIVE_AD) {
            return postList.get(position);
        }
        return null;
    }

}