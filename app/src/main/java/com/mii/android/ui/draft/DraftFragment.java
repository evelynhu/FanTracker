package com.mii.android.ui.draft;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mii.android.R;

import com.mii.android.model.Post;
import com.mii.android.model.Database;
import com.mii.android.model.Storage;
import com.mii.android.ui.FanTrackerActivity;
import com.mii.android.ui.base.BaseFragment;

import android.content.Context;
import android.widget.Toast;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * Created by mii on 2018/3/25.
 */

public class DraftFragment  extends BaseFragment {

    private static final String TAG = DraftFragment.class.getName();

    public static final int PICK_FROM_GALLERY = 1;

    @BindView(R.id.post_item_imageview)
    SimpleDraweeView mImageView;

    @BindView(R.id.post_item_caption)
    EditText mCaption;

    @BindView(R.id.post_item_profile_image)
    SimpleDraweeView mProfileImage;

    @BindView(R.id.post_item_profile_name)
    TextView mProfileName;

    @BindView(R.id.post_item_privacy)
    TextView mPrivacy;

    @BindView(R.id.post_item_button)
    Button mPostButton;

    private static StorageReference mStorageRef;

    // TODO
    private static final String PROFILE_NAME = "Coffee Break";
    private static final String PROFILE_PICTURE_URL = "https://firebasestorage.googleapis.com/v0/b/fantracker-390db.appspot.com/o/images%2F9d62873b-e205-45fe-a22e-78fad92fa7d0?alt=media&token=5ea11537-e0fa-43c2-8635-d3f0281c4722";

    private Uri mPictureLocalPath;

    Context mContext;

    private static FirebaseDatabase mDatabase;

    private static DatabaseReference mPostReference;

    public static DraftFragment newInstance() {
        DraftFragment fragment = new DraftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DraftFragment() {
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_draft;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = getContext();
        mStorageRef = Storage.getStorage();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initProfile();
    }

    private void initProfile() {
        mProfileName.setText(PROFILE_NAME);
        Uri uri = Uri.parse(PROFILE_PICTURE_URL);
        mProfileImage.setImageURI(uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(getContext(), "Need permission to choose image from gallery!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.initDatabase();
    }

    private void initDatabase() {
        mDatabase =  Database.getDatabase();
        mPostReference = mDatabase.getReference(Post.REF_POST);
    }

    @OnTextChanged(R.id.post_item_caption)
    protected void onTextChanged(CharSequence text) {
        String caption = text.toString();
    }

    @OnClick(R.id.post_item_imageview)
    public void chooseImage() {
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeNewPost(String downloadUrl) {
        Post post = new Post(this.mCaption.getText().toString(),
                downloadUrl,
                0,
                PROFILE_PICTURE_URL,
                PROFILE_NAME,
                System.currentTimeMillis());
        Log.d(TAG, "Write a post : " + post.toString());
        DatabaseReference newChildRef = mPostReference.push();
        newChildRef.setValue(post);
    }

    private void resetUI() {
        mPictureLocalPath = null;
        mCaption.setText("");
        mImageView.setImageResource(R.drawable.default_image);
        ((FanTrackerActivity) getActivity()).selectPage(FanTrackerActivity.PAGE_TAB_POSITION);
    }

    @OnClick(R.id.post_item_button)
    public void uploadImage() {
        if(mPictureLocalPath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            Log.d(TAG, "upload image from : " + mPictureLocalPath);
            StorageReference ref = mStorageRef.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(mPictureLocalPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            writeNewPost(taskSnapshot.getDownloadUrl().toString());
                            resetUI();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Cannot post the status at this time. Please check your connection and try again.", Toast.LENGTH_LONG).show();                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else {
            writeNewPost("");
            resetUI();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            mPictureLocalPath = data.getData();
            mImageView.setImageURI(mPictureLocalPath);
        }
    }
}
