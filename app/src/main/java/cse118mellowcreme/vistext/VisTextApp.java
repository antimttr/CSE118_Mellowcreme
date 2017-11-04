package cse118mellowcreme.vistext;

import android.app.Application;

/**
 * Created by MWEST on 11/3/2017.
 * Application for global object references
 */

public class VisTextApp extends Application {
    private static VisTextApp singleton;

    public static VisTextApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
