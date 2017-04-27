package com.sajorahasan.tiffincounter;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Sajora on 24-04-2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm. Should only be done once when the application starts.
        Realm.init(this);
    }
}
