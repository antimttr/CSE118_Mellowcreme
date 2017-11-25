package cse118mellowcreme.vistext;


import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GalleryInnerActivity extends AppCompatActivity {

    private String category;
    private String tagLabel;
    private CategoryMaps categoryMap;

    //navigate through all the pictures in the directory and find the ones with the correct category tags
    private List<File> getPicturesWithContext() {
        File storage = new File(GalleryInnerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "VisText");
        File[] allPictures = storage.listFiles();
        List<File> galleryPictures = new ArrayList<File>();

        for (File image : allPictures) {
            try {
                if (category.equals("All")) {
                    galleryPictures.add(image);
                } else {
                    ExifInterface exif = new ExifInterface(image.getAbsolutePath());
                    String tagList = exif.getAttribute(ExifInterface.TAG_USER_COMMENT);
                    if (exif != null && tagList != null && !tagList.equals("")) {
                        JSONArray json = new JSONArray(tagList);
                        if (categoryMap.isInCategory(category, json)) {
                            galleryPictures.add(image);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //add the picture or something to a list to show in the gallery
        return galleryPictures;
    }

    /**
     * Navigate through all the pictures in the directory and find the ones with the correct tags
     * @return
     */
    private List<File> getPicturesWithTagLabel() {
        File storage = new File(GalleryInnerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "VisText");
        File[] allPictures = storage.listFiles();
        List<File> galleryPictures = new ArrayList<File>();

        for (File image : allPictures) {
            try {
                    ExifInterface exif = new ExifInterface(image.getAbsolutePath());
                    String tagList = exif.getAttribute(ExifInterface.TAG_USER_COMMENT);
                    if (exif != null && tagList != null && !tagList.equals("") && !tagList.equals("?")) {
                        Log.e("gallery_inner", tagList);
                        JSONArray json = new JSONArray(tagList);
                        if (hasTag(tagLabel, json)) {
                            galleryPictures.add(image);
                        }
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //add the picture or something to a list to show in the gallery
        return galleryPictures;
    }

    public boolean hasTag(String tagStr, JSONArray labels) {
        if (tagStr != null) {
            for (int i = 0; i < labels.length(); i++) {
                try {
                    if (tagStr.equals((labels.get(i)))) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_inner);

        categoryMap = new CategoryMaps();
        categoryMap.buildCategories();

        TextView categoryName = (TextView)findViewById(R.id.textView2);
        Intent intent = getIntent();
        category = intent.getStringExtra("CategoryChosen");
        tagLabel = intent.getStringExtra("TagSearch");
        List<File> picPossible = null;
        if(category != null) {
            categoryName.setText(category);
            picPossible = getPicturesWithContext();
        }
        else if (tagLabel != null) {
            categoryName.setText(tagLabel);
            picPossible = getPicturesWithTagLabel();

            Log.e("tag_label", picPossible.toString());
        }
        final List<File> pictures = picPossible;

        if(pictures != null) {
            //set the picture files in the gallery
            GridView gridView = (GridView) findViewById(R.id.gridView2);
            gridView.setAdapter(new ImageAdapter(this, pictures));


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(GalleryInnerActivity.this, pictures.get(position).getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GalleryInnerActivity.this, ViewActivity.class);
                    intent.putExtra("file", pictures.get(position).getAbsolutePath());
                    startActivity(intent);
                }
            });

        }
        final Button button = (Button)findViewById(R.id.sortButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog();
            }
        });

        SearchView searchBar = (SearchView) findViewById(R.id.searchBar);
        searchBar.setIconifiedByDefault(false);
        searchBar.setQueryHint("Search tags");
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           public boolean onQueryTextChange(String newText) {

               ListView list = (ListView) findViewById(R.id.searchList);
               ArrayList<String> searchBarList = new ArrayList<String>();
               ListViewAdapter adapter;

               //when user erases all the text, clear the list view of options
               if (TextUtils.isEmpty(newText)) {
                   searchBarList.clear();
                   adapter = new ListViewAdapter(GalleryInnerActivity.this, searchBarList);
                   list.setAdapter(adapter);
                   adapter.filter(newText);
               } else {

                   // what happens when the user types stuff
                   if (!TextUtils.isEmpty(category)) {

                       //if the category is all, search from all available tags
                       if (category.equals("All")) {
                           TagMap tags = new TagMap();
                           tags.buildMap();
                           ArrayList<String> tagList = tags.getTagList();
                           if (tagList != null) {
                               for (int i = 0; i < tagList.size(); i++) {
                                   searchBarList.add(tagList.get(i));
                               }
                           }
                       } else { //need to grab list of tags from the category
                           List<String> tagList = categoryMap.convertStringToCategory(category);
                           if (tagList != null) {
                               for (int i = 0; i < tagList.size(); i++) {
                                   searchBarList.add(tagList.get(i));
                               }
                           }
                       }
                   }

                   adapter = new ListViewAdapter(GalleryInnerActivity.this, searchBarList);
                   list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                   adapter.filter(newText);

               }
               return false;
           }

           public boolean onQueryTextSubmit(String query) {

                // what happens when the user clicks submit

               return false;
           }
        });

    }

    public void openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.sort_dialog);
        dialog.setTitle("Sort By");
        dialog.show();
    }


}


