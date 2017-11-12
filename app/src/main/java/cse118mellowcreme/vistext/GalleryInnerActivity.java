package cse118mellowcreme.vistext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GalleryInnerActivity extends AppCompatActivity {

    private String category;
    private CategoryMaps categoryMap;

    private List<String> getPicturesWithContext() {
        //navigate through all the pictures in the gallery
        List<Object> allPictures = new ArrayList<>(); //contains all the picture objects?
        List<String> pictureLabels = new ArrayList<String>(); //contains the context of a picture
        List<String> galleryPictures = new ArrayList<String>();
        if (categoryMap != null) {
            for (int i = 0; i < allPictures.size(); i++) {
                //get the picture labels from the picture
                //pictureLabels = allPictures.getLabels();
                if (categoryMap.isInCategory(category, pictureLabels)) {
                    galleryPictures.add(""); //add the picture
                }
            }

        }
        //add the picture or something to a list to show in the gallery
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_inner);
        TextView categoryName = (TextView)findViewById(R.id.textView2);
        Intent intent = getIntent();
        category = intent.getStringExtra("CategoryChosen");
        categoryName.setText(category);

        categoryMap = new CategoryMaps();
        categoryMap.buildCategories();

        List<String> pictures = new ArrayList<String>(); //pictures that will be visible in the gallery
        pictures = getPicturesWithContext();

    }
}
