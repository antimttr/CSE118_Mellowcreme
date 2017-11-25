package cse118mellowcreme.vistext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class ViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private TagView tagView;

    public String getCurrentFile() {
        return currentFile;
    }

    private String currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View arg0) {
                refreshTags();
            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //shows the drawer if clicked
        ImageButton showDrawerButton = (ImageButton) findViewById(R.id.imageButton2);
        showDrawerButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                     drawer.openDrawer(GravityCompat.START);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
        });

        //tag view start
        View headerLayout = navigationView.getHeaderView(0);

        //starts facebook image upload process if pressed
        ImageButton uploadToFacebook = (ImageButton) headerLayout.findViewById(R.id.startFB);
        uploadToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("fb_login", "login acitivity called.");
                    if (AccessToken.getCurrentAccessToken() == null) {
                        Intent loginIntent = new Intent(ViewActivity.this, FacebookLoginActivity.class);
                        startActivity(loginIntent);
                    } else {
                        Intent uploadIntent = new Intent(ViewActivity.this, FacebookUploadActivity.class);
                        uploadIntent.putExtra("file", currentFile);
                        startActivity(uploadIntent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //starts facebook image upload process if pressed
        ImageButton facebookLogout = (ImageButton) headerLayout.findViewById(R.id.logoutFB);
        facebookLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("fb_logout", "facebook logout called.");
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




        tagView = (TagView) headerLayout.findViewById(R.id.tagview);
        //SET LISTENER
        tagView.setOnTagClickListener(new OnTagClickListener() {

            @Override
            public void onTagClick(int position, Tag tag) {
                Intent intent = new Intent(ViewActivity.this, GalleryInnerActivity.class);
                intent.putExtra("TagSearch",  tag.text);
                startActivity(intent);

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
                Picasso.with(this).load(jpgFile).fit().centerInside().into(imageView);
            }

            //add tag button inside drawer
            Button addTagsButton = (Button) headerLayout.findViewById(R.id.addTags);
            addTagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Addtags Button", "Clicked");
                    try {
                        //new ViewActivity.ConfirmationDialog().show(getSupportFragmentManager(), "dialog");

                        // custom dialog
                        final Dialog dialog = new Dialog(view.getContext());
                        view.getRootView().setClipToOutline(true);
                        dialog.setContentView(R.layout.activity_view_tag_dialog);
                        dialog.setTitle("Add Tag");

                        final RadioButton radioTextInput = (RadioButton) dialog.findViewById(R.id.radioButton);
                        final RadioButton radioTextInput2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
                        final EditText textInput = (EditText) dialog.findViewById(R.id.editText3);
                        final Spinner tagSelect = (Spinner) dialog.findViewById(R.id.spinner2);

                        tagSelect.setEnabled(false);
                        VisTextApp visTextApp = (VisTextApp)getApplication();
                        ArrayList<String> tagList =  visTextApp.getTagMaps().getTagList();
                        List<String> spinnerArray =  new ArrayList<String>();

                        for (String tag: tagList) {
                            spinnerArray.add(tag);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                view.getContext(), android.R.layout.simple_spinner_item, spinnerArray);

                        tagSelect.setAdapter(adapter);

                        radioTextInput.setChecked(true);
                        //select text input, and deselect tag select by menu
                        radioTextInput.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                textInput.setFocusable(true);
                                textInput.setEnabled(true);
                                textInput.setCursorVisible(true);
                                tagSelect.setEnabled(false);
                                radioTextInput.setChecked(true);
                                radioTextInput2.setChecked(false);
                            }
                        });

                        radioTextInput2.setChecked(false);
                        //selects menu tag selection, and deselect text input
                        radioTextInput2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                textInput.setFocusable(false);
                                textInput.setEnabled(false);
                                textInput.setCursorVisible(false);
                                tagSelect.setEnabled(true);
                                radioTextInput2.setChecked(true);
                                radioTextInput.setChecked(false);
                            }
                        });

                        Button dialogCancel = (Button) dialog.findViewById(R.id.button10);
                        // if button is clicked, close the custom dialog
                        dialogCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                dialog.dismiss();
                            }
                        });


                        Button dialogButton = (Button) dialog.findViewById(R.id.button9);
                        // if button is clicked, accept input and close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Log.i("confirmAddTagsButton", "Clicked");

                                    Editable input = textInput.getText();
                                    String inputString = radioTextInput.isChecked() ? input.toString() : tagSelect.getSelectedItem().toString();
                                    if(inputString != "") {
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
                                            String entry = json.get(i).toString();
                                            currentTags.add(entry);
                                        }
                                        if(!currentTags.contains(inputString))
                                            currentTags.add(inputString);
                                        JSONArray jsonOut = new JSONArray(currentTags);

                                        exif.setAttribute(ExifInterface.TAG_USER_COMMENT, jsonOut.toString());
                                        exif.saveAttributes();

                                        Log.e("tag_write", "Inserted tag id = "
                                                + inputString + " into " + jpgFile.getName());

                                        refreshTags();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });

                        dialog.show();



                        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        //drawer.closeDrawer(GravityCompat.START);
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
            Log.e("tag_read", exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
            JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
            for(int i=0; i < json.length(); i++) {
                Tag tag = new Tag((String)json.get(i).toString());
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

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final EditText taskEditText = new EditText(getContext());
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.add_tag)
                    .setView(taskEditText)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Log.i("confirmAddTagsButton", "Clicked");
                                ViewActivity viewActivity = (ViewActivity)getActivity();
                                Editable input = taskEditText.getText();
                                if(input.toString() != "") {
                                    File jpgFile = new File(viewActivity.getCurrentFile());
                                    if(!jpgFile.exists()){
                                        Log.e("tag write", "Jpg doesn't exist");
                                        return;
                                    }

                                    ExifInterface exif = new ExifInterface(jpgFile.getAbsolutePath());

                                    JSONArray json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                                    Log.e("JSON", json.toString());
                                    Vector<String> currentTags = new Vector<>();
                                    for(int i=0; i < json.length(); i++) {
                                        String entry = json.get(i).toString();
                                        currentTags.add(entry);
                                    }
                                    currentTags.add(input.toString());
                                    JSONArray jsonOut = new JSONArray(currentTags);

                                    exif.setAttribute(ExifInterface.TAG_USER_COMMENT, jsonOut.toString());
                                    exif.saveAttributes();

                                    Toast.makeText(getContext(), "Inserted tag id = "
                                            + input.toString() + " into " + jpgFile.getName(), Toast.LENGTH_SHORT).show();

                                    viewActivity.refreshTags();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                    .create();
        }
    }
}
