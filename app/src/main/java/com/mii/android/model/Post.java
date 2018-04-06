package com.mii.android.model;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Post {
    public static final String REF_POST = "posts";

    private String key;

//    private String authorId; // TODO
    private String profileName;
    private String profilePicUrl;

    private long timeStamp;
    private String caption;
    private String imageUrl;
    private long viewer;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Post(String caption, String imageUrl, long viewer, String profilePicUrl, String profileName, long timeStamp) {
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.viewer = viewer;
        this.profilePicUrl = profilePicUrl;
        this.profileName = profileName;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post Post = (Post) o;

        if (Long.compare(Post.viewer, viewer) != 0) return false;
        if (key != null ? !key.equals(Post.key) : Post.key != null) return false;
        if (caption != null ? !caption.equals(Post.caption) : Post.caption != null) return false;
        return imageUrl != null ? imageUrl.equals(Post.imageUrl) : Post.imageUrl == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = key != null ? key.hashCode() : 0;
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        temp = Double.doubleToLongBits(viewer);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Post{");
        sb.append("key='").append(key).append('\'');
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append(", viewer=").append(String.valueOf(viewer));
        sb.append('}');
        return sb.toString();
    }

    public void setRef(String key) {
        this.key = key;
    }

    public String getRef() {
        return key;
    }

    public void setCaption() {
         this.caption = caption;
    }

    public String getShortCaption() {
        return this.caption.substring(0, Math.min(10, this.caption.length())) + "...";
    }

    public String getCaption() {
        return caption;
    }

    public void setImageUrl() {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setViewer() {
        this.viewer = viewer;
    }

    public long getViewer() {
        return viewer;
    }

    public void setProfilePicUrl() {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfileName() {
        this.profileName = profileName;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setTimeStamp() {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("caption", caption);
        result.put("imageUrl", imageUrl);
        result.put("viewer", viewer);
        result.put("profilePicUrl", profilePicUrl);
        result.put("profileName", profileName);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
