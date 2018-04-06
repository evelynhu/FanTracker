package com.mii.android.ui;

import android.app.Application;

//import com.mii.android.inject.DefaultDependenciesFactory;
//import com.mii.android.inject.Injector;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mii.android.model.Database;
import com.mii.android.model.Storage;

public class FanTrackerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

}
