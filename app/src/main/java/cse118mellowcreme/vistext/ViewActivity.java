package cse118mellowcreme.vistext;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class ViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private TagView tagView;

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
        try {
            Tag tag = new Tag("Colorful Testing Tags!");

            tag.tagTextColor = Color.parseColor("#000000");
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);

            tag = new Tag("Test Tags!");
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#DDDDDD");
            tag.layoutColorPress = Color.parseColor("#555555");
            //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
            tag.radius = 20f;
            tag.tagTextSize = 14f;
            tag.layoutBorderSize = 1f;
            tag.layoutBorderColor = Color.parseColor("#FFFFFF");
            tag.isDeletable = true;
            tagView.addTag(tag);


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
