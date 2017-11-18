package cse118mellowcreme.vistext;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristin Agcaoili on 11/6/2017.
 */

public class CategoryMaps {

    private List<String> categoryActive = new ArrayList<String>();
    private List<String> categoryEntertainment = new ArrayList<String>();
    private List<String> categoryLifestyle = new ArrayList<String>();
    private List<String> categoryLocation = new ArrayList<String>();
    private List<String> categoryTravel = new ArrayList<String>();

    public void buildCategories() {
        categoryActive.add("standing");
        categoryActive.add("walking");
        categoryActive.add("running");
        categoryActive.add("exercising");
        categoryActive.add("strolling");
        categoryActive.add("workingOut");
        categoryActive.add("upstairs");
        categoryActive.add("downstairs");
        categoryEntertainment.add("lab");
        categoryEntertainment.add("cooking");
        categoryEntertainment.add("shopping");
        categoryEntertainment.add("watchingTV");
        categoryEntertainment.add("surfingTheNet");
        categoryEntertainment.add("partying");
        categoryEntertainment.add("bar");
        categoryEntertainment.add("beach");
        categoryEntertainment.add("singing");
        categoryEntertainment.add("computers");
        categoryEntertainment.add("coworkers");
        categoryEntertainment.add("friends");
        categoryEntertainment.add("drunk");
        categoryLifestyle.add("showering");
        categoryLifestyle.add("resting");
        categoryLifestyle.add("sitting");
        categoryLifestyle.add("cleaning");
        categoryLifestyle.add("laundry");
        categoryLifestyle.add("dishes");
        categoryLifestyle.add("talking");
        categoryLifestyle.add("eating");
        categoryLifestyle.add("toilet");
        categoryLifestyle.add("grooming");
        categoryLifestyle.add("gettingDressed");
        categoryLocation.add("meeting");
        categoryLocation.add("work");
        categoryLocation.add("indoors");
        categoryLocation.add("outside");
        categoryLocation.add("home");
        categoryLocation.add("school");
        categoryLocation.add("restaurant");
        categoryLocation.add("elevator");
        categoryTravel.add("bicycling");
        categoryTravel.add("car");
        categoryTravel.add("bus");
        categoryTravel.add("driving");
        categoryTravel.add("passenger");
    }

    private List<String> convertStringToCategory(String categoryStr) {
        if (categoryStr.equals("Active")) {
            return categoryActive;
        } else if (categoryStr.equals("Entertainment")) {
            return categoryEntertainment;
        } else if (categoryStr.equals("Lifestyle")) {
            return categoryLifestyle;
        } else if (categoryStr.equals("Location")) {
            return categoryLocation;
        } else if (categoryStr.equals("Travel")) {
            return categoryTravel;
        }
        return null;
    }

    public boolean isInCategory(String categoryStr, JSONArray labels) {

        List<String> category = convertStringToCategory(categoryStr);

        if (category != null) {
            for (int i = 0; i < labels.length(); i++) {
                try {
                    //Log.d("GALLERY", "checking labels " + i + " is: " + labels.get(i));
                    if (category.contains((labels.get(i)))) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


}
