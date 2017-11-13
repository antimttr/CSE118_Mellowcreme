package cse118mellowcreme.vistext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.util.Vector;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class ViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private TagView tagView;
    private String currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //tag view start
        View headerLayout = navigationView.getHeaderView(0);
        tagView = (TagView) headerLayout.findViewById(R.id.tagview);
        //SET LISTENER
        tagView.setOnTagClickListener(new OnTagClickListener() {

            @Override
            public void onTagClick(int position, Tag tag) {
                //Toast.makeText(ViewActivity.this, "click tag id = " + tag.id + " position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        tagView.setOnTagDeleteListener(new OnTagDeleteListener() {

            @Override
            public void onTagDeleted(int position, Tag tag) {
                try {
                    File jpgFile = new File(currentFile);
                    if (jpgFile.exists()) {
                        Toast.makeText(ViewActivity.this, "Deleted tag id = "
                                + tag.text + " from " + jpgFile.getName(), Toast.LENGTH_SHORT).show();

                        ExifInterface exif = new ExifInterface(jpgFile.getAbsolutePath());

                        JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                        Vector<String> currentTags = new Vector<>();
                        for (int i = 0; i < json.length(); i++) {
                            String entry = (String) json.get(i);
                            if(!entry.toString().equals(tag.text))
                                    currentTags.add(entry.toString());
                        }

                        JSONArray jsonOut = new JSONArray(currentTags);
                        exif.setAttribute(ExifInterface.TAG_USER_COMMENT, jsonOut.toString());
                        exif.saveAttributes();

                        refreshTags();
                    }
                } catch (Exception e) {

                }
            }
        });

        try {

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            currentFile = extras.getString("file");
            Log.i("tag_read", currentFile);
            File jpgFile = new File(currentFile);
            if(jpgFile.exists()) {
                refreshTags();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                Picasso.with(this).load(jpgFile).fit().centerCrop().into(imageView);
            }


            ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.view_content_layout);
            constraintLayout.findViewById(R.id.button7).setVisibility(View.GONE);
            constraintLayout.findViewById(R.id.button8).setVisibility(View.GONE);
            constraintLayout.findViewById(R.id.editText).setVisibility(View.GONE);
            constraintLayout.findViewById(R.id.textView).setVisibility(View.GONE);


            Button addTagsButton = (Button) headerLayout.findViewById(R.id.addTags);
            addTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Addtags Button", "Clicked");
                    try {
                        EditText editText = (EditText)findViewById(R.id.editText);
                        editText.setText("");
                        findViewById(R.id.button7).setVisibility(View.VISIBLE);
                        findViewById(R.id.button8).setVisibility(View.VISIBLE);
                        findViewById(R.id.editText).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView).setVisibility(View.VISIBLE);
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Button cancelAddTagsButton = (Button) constraintLayout.findViewById(R.id.button8);
            cancelAddTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.i("CancelAddTags Button", "Clicked");
                        EditText editText = (EditText)findViewById(R.id.editText);
                        editText.setText("");
                        findViewById(R.id.button7).setVisibility(View.GONE);
                        findViewById(R.id.button8).setVisibility(View.GONE);
                        findViewById(R.id.editText).setVisibility(View.GONE);
                        findViewById(R.id.textView).setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            Button confirmAddTagsButton = (Button) constraintLayout.findViewById(R.id.button7);
            confirmAddTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.i("confirmAddTagsButton", "Clicked");
                        EditText editText = (EditText)findViewById(R.id.editText);
                        Editable input = editText.getText();
                        if(input.toString() != "") {
                            File jpgFile = new File(currentFile);
                            if(!jpgFile.exists()){
                                Log.e("tag write", "Jpg doesn't exist");
                                return;
                            }

                            ExifInterface exif = new ExifInterface(jpgFile.getAbsolutePath());

                            JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                            Log.e("JSON", json.toString());
                            Vector<String> currentTags = new Vector<>();
                            for(int i=0; i < json.length(); i++) {
                                String entry = (String)json.get(i).toString();
                                currentTags.add(entry);
                            }
                            currentTags.add(input.toString());
                            JSONArray jsonOut = new JSONArray(currentTags);

                            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, jsonOut.toString());
                            exif.saveAttributes();

                            Toast.makeText(ViewActivity.this, "Inserted tag id = "
                                    + input.toString() + " into " + jpgFile.getName(), Toast.LENGTH_SHORT).show();

                            refreshTags();
                            editText.setText("");
                            findViewById(R.id.button7).setVisibility(View.GONE);
                            findViewById(R.id.button8).setVisibility(View.GONE);
                            findViewById(R.id.editText).setVisibility(View.GONE);
                            findViewById(R.id.textView).setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("currentFile",  currentFile);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFile = savedInstanceState.getString("currentFile");

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Refreshes the list of tags in the drawer
     */
    private void refreshTags() {
        if(tagView == null) {
            Log.e("tag read", "TagView null");
            return;
        }

        if(currentFile == null) {
            Log.e("tag read", "Current file null");
            return;
        }

        File jpgFile = new File(currentFile);
        if(!jpgFile.exists()){
            Log.e("tag read", "Jpg doesn't exist");
            return;
        }

        try {
            tagView.removeAllTags();

            ExifInterface exif = new ExifInterface(jpgFile.getAbsolutePath());
            JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
            for(int i=0; i < json.length(); i++) {
                Tag tag = new Tag((String)json.get(i));
                tag.tagTextColor = Color.parseColor("#000000");
                tag.layoutColor = Color.parseColor("#FFFFFF");
                tag.layoutColorPress = Color.parseColor("#000000");
                tag.deleteIndicatorColor = Color.parseColor("#000000");
                //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                tag.radius = 20f;
                tag.tagTextSize = 14f;
                tag.layoutBorderSize = 1f;
                tag.layoutBorderColor = Color.parseColor("#555555");
                tag.isDeletable = true;
                tagView.addTag(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
