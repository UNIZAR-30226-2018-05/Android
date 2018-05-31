package com.example.android.mediasession;

import android.app.Application;
import android.content.res.Configuration;

import cierzo.model.objects.Session;
import cierzo.model.objects.UserLogged;

public class CierzoApp extends Application {
    public UserLogged mUserLogged;
    public Session mSession;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        mUserLogged = UserLogged.Companion.getInstance();
        mSession = Session.Companion.getInstance();
        // Required initialization logic here!
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
