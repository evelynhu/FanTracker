package com.mii.android.ui.page;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.mii.android.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mii on 2018/3/26.
 */

public class NativeAdViewHolder  extends RecyclerView.ViewHolder {
    @BindView(R.id.adImage)
    ImageView adImage;

    @BindView(R.id.tvAdTitle)
    TextView tvAdTitle;

    @BindView(R.id.tvAdBody)
    TextView tvAdBody;

    @BindView(R.id.btnCTA)
    TextView btnCTA;

    View container;

    @BindView(R.id.adChoicesContainer)
    LinearLayout adChoicesContainer;

    @BindView(R.id.mediaView)
    MediaView mediaView;

    AdChoicesView adChoicesView;

    NativeAdViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        this.container = itemView;
    }

    void setAdChoicesView(AdChoicesView adChoicesView) {
        this.adChoicesView = adChoicesView;
    }
}