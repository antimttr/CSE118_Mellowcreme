package cse118mellowcreme.vistext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kristin Agcaoili on 11/4/2017.
 */

public class TagMap {

    private HashMap<String, String> tagMap = new HashMap<String, String>();

    public void buildMap() {
        tagMap.put("Lying down", "resting");
        tagMap.put("Sitting", "sitting");
        tagMap.put("Standing", "standing");
        tagMap.put("Walking", "walking");
        tagMap.put("Running", "running");
        tagMap.put("Bicycling", "bicycling");
        tagMap.put("Lab work", "lab");
        tagMap.put("In a meeting", "meeting");
        tagMap.put("At work", "work");
        tagMap.put("Indoors", "indoors");
        tagMap.put("Outside", "outside");
        tagMap.put("In a car", "car");
        tagMap.put("On a bus", "bus");
        tagMap.put("Drive – I’m the driver", "driving");
        tagMap.put("Driver – I’m the passenger", "passenger");
        tagMap.put("At home", "home");
        tagMap.put("At school", "school");
        tagMap.put("At a restaurant", "restaurant");
        tagMap.put("Exercising", "exercising");
        tagMap.put("Cooking", "cooking");
        tagMap.put("Shopping", "shopping");
        tagMap.put("Strolling", "strolling");
        tagMap.put("Drinking (alcohol)", "drunk");
        tagMap.put("Bathing – shower", "showering");
        tagMap.put("Cleaning", "cleaning");
        tagMap.put("Doing laundry", "laundry");
        tagMap.put("Washing dishes", "dishes");
        tagMap.put("Watching TV", "watchingTV");
        tagMap.put("Surfing the internet", "surfingTheNet");
        tagMap.put("At a party", "partying");
        tagMap.put("At a bar", "bar");
        tagMap.put("At the beach", "beach");
        tagMap.put("Singing", "singing");
        tagMap.put("Talking", "talking");
        tagMap.put("Computer work", "computers");
        tagMap.put("Eating", "eating");
        tagMap.put("Toilet", "toilet");
        tagMap.put("Grooming", "grooming");
        tagMap.put("Dressing", "gettingDressed");
        tagMap.put("At the gym", "workingOut");
        tagMap.put("Stairs – going up", "upstairs");
        tagMap.put("Stairs – going down", "downstairs");
        tagMap.put("Elevator", "elevator");
        tagMap.put("Phone in pocket", "");
        tagMap.put("Phone in hand", "");
        tagMap.put("Phone in bag", "");
        tagMap.put("Phone on table", "");
        tagMap.put("With co-workers", "coworkers");
        tagMap.put("With friends", "friends");
    }

    public ArrayList<String> getTagList() {
        ArrayList<String> result = new ArrayList<>();
        Iterator it = tagMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(!pair.getValue().equals("")) {
                result.add((String)pair.getValue());
            }
        }
        return result;
    }
    
    public String getTag(String label) {
        return tagMap.get(label);
    }

    public void putTag(String label, String tag) {
        tagMap.put(label, tag);
    }

}
