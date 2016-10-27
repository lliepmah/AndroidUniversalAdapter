package ru.lliepmah.sample.model;

import android.app.Activity;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

public class Screen {
    private String title;
    private Class<? extends Activity> activityClass;

    public Screen(String title, Class<? extends Activity> activityClass) {
        this.title = title;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }
}
