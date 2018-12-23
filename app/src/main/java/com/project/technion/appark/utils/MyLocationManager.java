package com.project.technion.appark.utils;

public class MyLocationManager {
    private static final MyLocationManager ourInstance = new MyLocationManager();

    public static MyLocationManager getInstance() {
        return ourInstance;
    }

    private MyLocationManager() {
    }



}
