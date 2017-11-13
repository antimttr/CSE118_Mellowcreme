package cse118mellowcreme.vistext;

import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class GalleryInnerActivity extends AppCompatActivity {

    private String category;
    private CategoryMaps categoryMap;

    //navigate through all the pictures in the directory and find the ones with the correct category tags
    private List<File> getPicturesWithContext() {
        File storage = new File(GalleryInnerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "VisText");
        File[] allPictures = storage.listFiles();
        List<File> galleryPictures = new ArrayList<File>();

        for(File image : allPictures) {
            try {
                ExifInterface exif = new ExifInterface(image.getAbsolutePath());
                JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                if (categoryMap.isInCategory(category, json)) {
                    galleryPictures.add(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //add the picture or something to a list to show in the gallery
        return galleryPictures;
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

        List<File> pictures = new ArrayList<File>(); //pictures that will be visible in the gallery
        pictures = getPicturesWithContext();

        //set the picture files in the gallery
        GridView gridView = (GridView) findViewById(R.id.gridView2);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(GalleryInnerActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }
}


