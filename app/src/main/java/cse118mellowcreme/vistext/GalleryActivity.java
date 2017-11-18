package cse118mellowcreme.vistext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }

    public void viewAll(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "All");
        startActivity(intent);
    }

    public void viewActive(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "Active");
        startActivity(intent);
    }

    public void viewEntertainment(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "Entertainment");
        startActivity(intent);
    }

    public void viewLifestyle(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "Lifestyle");
        startActivity(intent);
    }

    public void viewLocation(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "Location");
        startActivity(intent);
    }

    public void viewTravel(View view) {
        Intent intent = new Intent(this, GalleryInnerActivity.class);
        intent.putExtra("CategoryChosen", "Travel");
        startActivity(intent);
    }
}
