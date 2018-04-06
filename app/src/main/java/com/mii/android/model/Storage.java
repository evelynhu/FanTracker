package com.mii.android.model;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Storage {
    private static StorageReference mStorageRef;

    public static StorageReference getStorage() {
        if (mStorageRef == null) {
            //if we get interrupted for more than 2 seconds, fail the operation.
            FirebaseStorage.getInstance().setMaxUploadRetryTimeMillis(2000);
            mStorageRef =  FirebaseStorage.getInstance().getReference();
        }
        return mStorageRef;
    }

}