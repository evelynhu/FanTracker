package com.mii.android.ui.page;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mii.android.R;
import com.mii.android.model.Post;
import com.mii.android.ui.util.DateFormatter;
import com.mii.android.ui.util.ViewerFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mii on 2018/3/26.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.post_item_imageview)
    SimpleDraweeView image;

    @BindView(R.id.post_item_caption)
    TextView caption;

    @BindView(R.id.post_item_viewer)
    TextView viewer;

    @BindView(R.id.post_item_profile_image)
    SimpleDraweeView profileImage;

    @BindView(R.id.post_item_profile_name)
    TextView profileName;

    @BindView(R.id.post_item_timestamp)
    TextView timestamp;

    PostViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Post post) {
        caption.setText(post.getCaption());
        profileName.setText(post.getProfileName());
        timestamp.setText(DateFormatter.getRelativeTime(post.getTimeStamp()));
        viewer.setText(ViewerFormatter.format(post.getViewer()));

        if (post.getImageUrl() != null && !post.getImageUrl().trim().isEmpty()) {
            image.setImageURI(Uri.parse(post.getImageUrl()));
        }

        if (post.getImageUrl() != null && !post.getImageUrl().trim().isEmpty()) {
            profileImage.setImageURI(Uri.parse(post.getProfilePicUrl()));
        }
    }
}