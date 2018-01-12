package cse118mellowcreme.vistext;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by MWEST on 11/3/2017.
 * Application for global object references
 */

public class VisTextApp extends Application {
    private static VisTextApp singleton;
    private static VisTextContexts contexts;
    private static TagMap tagMap;

    public static VisTextApp getInstance() {
        return singleton;
    }

    public TagMap getTagMaps() { return tagMap; }
    public VisTextContexts getContexts() { return contexts; }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        contexts = new VisTextContexts();
        tagMap = new TagMap();
        tagMap.buildMap();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
