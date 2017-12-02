package cse118mellowcreme.vistext;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FacebookUploadActivity extends AppCompatActivity {
    /**
     * Tag view member for tag display
     */
    private TagView tagView;


    /**
     * String that contains the path to the file being uploaded
     */
    private String currentFile;

    private JSONArray json;

    private ArrayList<Boolean> tagSelected;

    public static CallbackManager callbackManager;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    /*private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };*/
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

   public void getPublishPermissions() {
        Collection<String> publishPermissions = new ArrayList<>();
        publishPermissions.add("publish_actions");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // User succesfully login with all permissions
                        // After this with these json and ParseUser , you can save your user to Parse

                        Log.i("fb_upload","FB login success.");
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                Log.i("fb_upload","FB login success.");
            }

            @Override
            public void onCancel() {
                Log.i("fb_upload","FB login canceled.");
                finish();
            }

            @Override
            public void onError(FacebookException facebookException) {
                Log.i("fb_upload","FB login error." + facebookException.toString());
                finish();
            }
        });
        LoginManager.getInstance().logInWithPublishPermissions(FacebookUploadActivity.this, publishPermissions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if not logged in then end activity

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //grab publish permissions.
        callbackManager = CallbackManager.Factory.create();
        getPublishPermissions();

        setContentView(R.layout.activity_facebook_upload);
        tagSelected = new ArrayList<>();
        mVisible = true;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        currentFile = extras.getString("file");
        File jpgFile = new File(currentFile);
        if(jpgFile.exists()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView_fb_upload);
            Picasso.with(this).load(jpgFile).fit().centerInside().into(imageView);

            tagView = (TagView) findViewById(R.id.tagview_fb_up);
            try {
                if(tagView != null) {
                    tagView.removeAllTags();

                    ExifInterface exif = new ExifInterface(jpgFile.getAbsolutePath());
                    Log.e("tag_read", exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                    json = new JSONArray(exif.getAttribute(ExifInterface.TAG_USER_COMMENT));
                    for (int i = 0; i < json.length(); i++) {
                        Tag tag = new Tag((String) json.get(i).toString());
                        tag.tagTextColor = Color.parseColor("#000000");
                        tag.layoutColor = Color.parseColor("#FFFFFF");
                        tag.layoutColorPress = Color.parseColor("#000000");
                        tag.deleteIndicatorColor = Color.parseColor("#000000");
                        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                        tag.radius = 20f;
                        tag.tagTextSize = 14f;
                        tag.layoutBorderSize = 1f;
                        tag.layoutBorderColor = Color.parseColor("#555555");
                        tag.isDeletable = false;
                        tagView.addTag(tag);
                        tagSelected.add(false);
                    }

                    //SET LISTENER
                    tagView.setOnTagClickListener(new OnTagClickListener() {

                        @Override
                        public void onTagClick(int position, Tag tag) {
                            activateTag(tag);
                            //Toast.makeText(FacebookUploadActivity.this, "click tag id = " + tag.id + " position = " + position, Toast.LENGTH_SHORT).show();
                        }
                    });


                    final ProgressDialog nDialog;
                    nDialog = new ProgressDialog(FacebookUploadActivity.this);
                    nDialog.setMessage("Uploading Image..");
                    nDialog.setTitle("Facebook upload");
                    nDialog.setIndeterminate(false);
                    nDialog.setCancelable(false);


                    //starts facebook image upload process if pressed
                    final ImageButton uploadToFacebook = (ImageButton) findViewById(R.id.imageButton2_fb_upload);
                    uploadToFacebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                uploadToFacebook.setClickable(false);
                                nDialog.show();
                                File jpgFile = new File(currentFile);
                                if(jpgFile.exists()) {
                                    Bitmap image = BitmapFactory.decodeFile(currentFile);
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setBitmap(image)
                                            .setCaption(buildCaption())
                                            .build();

                                    SharePhotoContent content = new SharePhotoContent.Builder()
                                            .addPhoto(photo)
                                            .build();

                                    ShareApi.share(content,  new FacebookCallback<Sharer.Result>() {
                                        String TAG = "facebook_upload";
                                        @Override
                                        public void onSuccess(Sharer.Result result) {
                                            Log.d(TAG, "SUCCESS");
                                            Toast.makeText(FacebookUploadActivity.this,
                                                        "Image Posted Successfully.",
                                                            Toast.LENGTH_SHORT).show();
                                            nDialog.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onCancel() {
                                            Log.d(TAG, "CANCELLED");
                                            Toast.makeText(FacebookUploadActivity.this,
                                                    "Image Posting canceled.",
                                                    Toast.LENGTH_SHORT).show();
                                            nDialog.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onError(FacebookException error) {
                                            Log.d(TAG, error.toString());
                                            Toast.makeText(FacebookUploadActivity.this,
                                                    "Image Posting error.",
                                                        Toast.LENGTH_SHORT).show();
                                            nDialog.dismiss();
                                            finish();
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private String buildCaption() {
        String result = "";
        List<Tag> tagList = tagView.getTags();
        int i = 0;
        for(Tag tagItem : tagList) {
            if(tagSelected.get(i)) {
                result = result + " #" + tagItem.text;
            }
            i++;
        }
        return result;
    }

    private void activateTag(Tag tag) {
        //find key of passed tag via tag text string
        List<Tag> tagList = tagView.getTags();
        //ArrayList<Tag> tagList1 = ArrayList<Tag>(tagList);
        String tagText = tag.text;
        tagView.removeAllTags();
        try {
            for (int i = 0; i < json.length(); i++) {
                Tag newTag = new Tag((String) json.get(i).toString());

                //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
                newTag.radius = 20f;
                newTag.tagTextSize = 14f;
                newTag.layoutBorderSize = 1f;
                newTag.layoutBorderColor = Color.parseColor("#555555");
                newTag.isDeletable = false;

                if(newTag.text.equals(tagText)) {
                    if(tagSelected.get(i))
                        tagSelected.set(i, false);
                    else
                        tagSelected.set(i, true);
                }

                if (tagSelected.get(i)) {
                    newTag.tagTextColor = Color.parseColor("#FFFFFF");
                    newTag.layoutColor = Color.parseColor("#000000");
                    newTag.layoutColorPress = Color.parseColor("#FFFFFF");
                } else {
                    newTag.tagTextColor = Color.parseColor("#000000");
                    newTag.layoutColor = Color.parseColor("#FFFFFF");
                    newTag.layoutColorPress = Color.parseColor("#000000");
                }

                tagView.addTag(newTag);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        //mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        //mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
