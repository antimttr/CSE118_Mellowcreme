package cse118mellowcreme.vistext;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
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
import java.util.Vector;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class GalleryInnerActivity extends AppCompatActivity {

    private String category;
    private String tagLabel;
    private CategoryMaps categoryMap;

    //navigate through all the pictures in the directory and find the ones with the correct category tags
    private List<File> getPicturesWithContext() {
        File storage = new File(GalleryInnerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + "VisText");
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
        File storage = new File(GalleryInnerActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + "VisText");
        File[] allPictures = storage.listFiles();
        List<File> galleryPictures = new ArrayList<File>();

        for (File image : allPictures) {
            try {
                    ExifInterface exif = new ExifInterface(image.getAbsolutePath());
                    String tagList = exif.getAttribute(ExifInterface.TAG_USER_COMMENT);
                    if (exif != null && tagList != null && !tagList.equals("") && !tagList.equals("?")) {
                        //Log.e("gallery_inner", tagList);
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

        updateGallery(picPossible);


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

    private void refreshGallery() {
        List<File> picPossible = null;
        if(category != null) {
            picPossible = getPicturesWithContext();
        }
        else if (tagLabel != null) {
            picPossible = getPicturesWithTagLabel();

            Log.e("tag_label", picPossible.toString());
        }
        final List<File> pictures = picPossible;

        updateGallery(pictures);
    }

    private void updateGallery(final List<File> pictures) {
        if(pictures != null) {


            //set the picture files in the gallery
            GridView gridView = (GridView) findViewById(R.id.gridView2);
            //clear previous gridview contents
            gridView.setAdapter(null);

            gridView.setAdapter(new ImageAdapter(this, pictures));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(GalleryInnerActivity.this, pictures.get(position).getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GalleryInnerActivity.this, ViewActivity.class);
                    intent.putExtra("file", pictures.get(position).getAbsolutePath());
                    startActivity(intent);
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                    final Dialog dialog = new Dialog(v.getContext());
                    Log.i("item_button", "long click sent");
                    // custom dialog
                    v.getRootView().setClipToOutline(true);
                    dialog.setContentView(R.layout.galleryinner_item_action_dialog);
                    dialog.setTitle("Item Menu");
                    final int pos = position;

                    //starts facebook image upload process if pressed
                    ImageButton facebookPost = (ImageButton) dialog.findViewById(R.id.imageButton4);
                    facebookPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            try {
                                if (AccessToken.getCurrentAccessToken() == null) {
                                    Intent loginIntent = new Intent(GalleryInnerActivity.this,
                                            FacebookLoginActivity.class);
                                    startActivity(loginIntent);
                                } else {
                                    Intent uploadIntent = new Intent(GalleryInnerActivity.this,
                                            FacebookUploadActivity.class);
                                    uploadIntent.putExtra("file", pictures.get(pos).getAbsolutePath());
                                    startActivity(uploadIntent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //prompts the user to rename a file if pressed
                    ImageButton renameItem = (ImageButton) dialog.findViewById(R.id.imageButton6);
                    renameItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            final EditText taskEditText = new EditText(view.getContext());
                            String path = pictures.get(pos).getName();
                            path = path.substring(0, path.lastIndexOf("."));
                            taskEditText.setSelectAllOnFocus(true);
                            taskEditText.setText(path);

                            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            try {
                                AlertDialog.Builder prompt = new AlertDialog.Builder(view.getContext());
                                prompt.setMessage(R.string.rename_file);
                                prompt.setView(taskEditText);
                                prompt.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imm.hideSoftInputFromInputMethod(getCurrentFocus().getRootView().getWindowToken(), 0);
                                        try {
                                            Log.i("confirmAddTagsButton", "Clicked");
                                            Editable input = taskEditText.getText();
                                            if(input.toString() != "") {
                                                Log.i("rename_file", "File rename started");
                                                File jpgFile = new File(pictures.get(pos).getAbsolutePath());
                                                if (jpgFile.exists()) {
                                                    File renamedFile = new File(jpgFile.getAbsolutePath().substring(0,
                                                            jpgFile.getAbsolutePath().lastIndexOf("/"))
                                                            + "/" + input.toString().trim() + ".jpg");
                                                    boolean result = jpgFile.renameTo(renamedFile);
                                                    if(result) {
                                                        Log.i("rename_file", "File Renamed");
                                                        Toast.makeText(GalleryInnerActivity.this,
                                                                "renamed to: " + renamedFile.getName(),
                                                                Toast.LENGTH_SHORT).show();
                                                        refreshGallery();
                                                    } else {
                                                        Log.e("rename_file", "File Rename failed");
                                                        Toast.makeText(GalleryInnerActivity.this,
                                                                "rename failed",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                            }
                                            } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                prompt.setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            imm.hideSoftInputFromInputMethod(getCurrentFocus().getRootView().getWindowToken(), 0);
                                        }
                                    })
                                        .create();
                                prompt.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //deletes the item from the file system if pressed
                    ImageButton deleteItem = (ImageButton) dialog.findViewById(R.id.imageButton5);
                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            try {
                                Log.i("delete_dialog", "Shown");
                                AlertDialog.Builder prompt;
                                prompt =   new AlertDialog.Builder(view.getContext());

                                prompt.setMessage(R.string.delete_confirm);
                                prompt.setCancelable(true);
                                prompt.setPositiveButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        File toBeDeleted = new File(pictures.get(pos).getAbsolutePath());
                                        if(toBeDeleted.exists()) {
                                            boolean deleted = toBeDeleted.delete();
                                            if(deleted) {
                                                Log.i("delete_file", "File Deleted");
                                                Toast.makeText(GalleryInnerActivity.this,
                                                 "deleted " + pictures.get(pos).getAbsolutePath(),
                                                     Toast.LENGTH_SHORT).show();
                                                refreshGallery();
                                            }
                                            else
                                                Log.i("delete_file", "File Delete faile");
                                                Toast.makeText(GalleryInnerActivity.this,
                                                        "deletion failed " + pictures.get(pos).getAbsolutePath(),
                                                        Toast.LENGTH_SHORT).show();
                                        }
                                    }});

                                prompt.setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }});
                                prompt.create();
                                prompt.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    dialog.show();
                    return true;
                }
            });
        }
    }

    public void openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.sort_dialog);
        dialog.setTitle("Sort By");
        dialog.show();
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
                    .setCancelable(true)
                    /*.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })*/
                    .create();
        }
    }

}


