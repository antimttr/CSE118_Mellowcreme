package cse118mellowcreme.vistext;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageButton;

import java.io.File;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import okhttp3.OkHttpClient;

/**
 * Created by kimberly on 11/27/17.
 */

public class ClarifaiService extends IntentService {
    private ClarifaiClient client;

    public ClarifaiService() {
        super("Clarifai Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CLARIFAI SERVICE", "On create");
        client = new ClarifaiBuilder(getResources().getString(R.string.clarifai_api))
                .client(new OkHttpClient())
                .buildSync();
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        if (workIntent == null) {
            Log.d("KIMBERLY!", "your work intent is null");
        }
        // Gets data from the incoming Intent
        String fileName = workIntent.getExtras().getString("image");
        Log.d("CLARIFAI SERVICE", fileName);
        ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                .withInputs(ClarifaiInput.forImage(new File(fileName)))
                .executeSync();

        sendBroadcast(response);
    }

    private void sendBroadcast (ClarifaiResponse clarifaiResponse) {
        Intent intent = new Intent ("predictions"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("predictions", clarifaiResponse.rawBody());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
