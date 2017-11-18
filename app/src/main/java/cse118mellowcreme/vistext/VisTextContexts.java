package cse118mellowcreme.vistext;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Created by MWEST on 11/4/2017.
 * This class creates a list of the current context tags gathered from Extrasensory. It is updated
 * every 60 seconds which is called by the CameraFragment.
 */

public class VisTextContexts {

    private Vector<String> tagList;
    private ArrayList<Pair<String, Double>> rawPredictions;

    private final String UUID_DIR_PREFIX = "extrasensory.labels.";
    private final String SERVER_PREDICTIONS_FILE_SUFFIX = ".server_predictions.json";
    private final String USER_REPORTED_LABELS_FILE_SUFFIX = ".user_reported_labels.json";

    public VisTextContexts() {
        tagList = new Vector<>();
    }

    /**
     *
     * @return
     */
    public Vector<String> getTags() {return (Vector<String>)tagList.clone();}

    /**
     * Checks ES data directory for new ES file, updates context tag list with latest contexts.
     * Should be called every 60 seconds.
     * @param currentActivity
     * @throws PackageManager.NameNotFoundException
     * @throws java.lang.NullPointerException
     */
    public void checkTags(FragmentActivity currentActivity)  throws PackageManager.NameNotFoundException, java.lang.NullPointerException {
       //List<String> users  = getUsers(currentActivity);
       //Log.e("Check data", users.toString());

        final String UUID_DIR_PREFIX = "extrasensory.labels.";
        final String SERVER_PREDICTIONS_FILE_SUFFIX = ".server_predictions.json";
        final String USER_REPORTED_LABELS_FILE_SUFFIX = ".user_reported_labels.json";

        // Locate the ESA saved files directory, and the specific minute-example's file:
        Context extraSensoryAppContext = currentActivity.getApplicationContext().
                createPackageContext("edu.ucsd.calab.extrasensory",0);
        File esaFilesDir = new File(extraSensoryAppContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());

        if (!esaFilesDir.exists()) {
            return;
        }

        //Log.e("Check Tags", esaFilesDir.list().toString());
        String[] filenames = esaFilesDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.startsWith(UUID_DIR_PREFIX);
            }
        });

        SortedSet<String> usersSet = new TreeSet<>();
        for (String filename : filenames) {
            String uuidPrefix = filename.replace(UUID_DIR_PREFIX,"");
            usersSet.add(uuidPrefix);
        }

        List<String> uuidPrefixes = new ArrayList<>(usersSet);
        String uuidPrefix = uuidPrefixes.get(0);

        esaFilesDir = new File(extraSensoryAppContext.
                getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),UUID_DIR_PREFIX + uuidPrefix);

        filenames = esaFilesDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(SERVER_PREDICTIONS_FILE_SUFFIX) || s.endsWith(USER_REPORTED_LABELS_FILE_SUFFIX);
            }
        });

        SortedSet<String> userTimestampsSet = new TreeSet<>();
        for (String filename : filenames) {
            String timestamp = filename.substring(0,10); // The timestamps always occupy 10 characters
            userTimestampsSet.add(timestamp);
        }

        List<String> userTimestamps = new ArrayList<>(userTimestampsSet);

        String timestamp = userTimestamps.get(userTimestamps.size() - 1);
        Log.i("Latest Tag Timestamp", timestamp);
        File minuteLabelsFile = new File(esaFilesDir,timestamp + SERVER_PREDICTIONS_FILE_SUFFIX);

        // Read the file:
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(minuteLabelsFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        rawPredictions = new ArrayList<>();

        try {
            JSONObject jObj = new JSONObject( URLDecoder.decode( text.toString(), "UTF-8" ));
            JSONArray jLabels = jObj.getJSONArray("label_names");
            for (int i=0; i < jLabels.length(); i++) {
                String obj =  (String)jLabels.get(i);
                rawPredictions.add(Pair.create(obj, 0.0d));
            }
            JSONArray jProbs = jObj.getJSONArray("label_probs");
            for (int i=0; i < jProbs.length(); i++) {
                Double obj = (Double)jProbs.get(i);
                rawPredictions.set(i, Pair.create(rawPredictions.get(i).first, obj));
            }

            tagList = new Vector<>();
            for(Pair<String, Double> prediction : rawPredictions) {
                if(prediction.second > 0.5f) {
                    VisTextApp app = (VisTextApp)currentActivity.getApplication();
                    String result = app.getTagMaps().getTag(prediction.first);

                    if(result != null)
                        if(!result.equals("") || !result.equals("null")) {
                            tagList.add(result);
                        }
                }
            }
            tagList.remove(tagList.size() - 1);
            Log.i("Last Tag Data Read", tagList.toString() + "# of els: " + tagList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
