package cse118mellowcreme.vistext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Kristin Agcaoili on 11/12/2017.
 * Citing Android Official Developer Guide as source
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<File> galleryPictures;

    public ImageAdapter(Context c, List<File> pictures) {
        mContext = c;
        galleryPictures = pictures;
    }

    public int getCount() {
        //return mThumbIds.length;
        return galleryPictures.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(470, 510));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        /*
        FileInputStream fs = null;
        Bitmap bm;
        //BitmapFactory.Options bfOptions = new BitmapFactory.Options();
        //bfOptions.inDither = false;
        //bfOptions.inPurgeable = true;

        try {
            fs = new FileInputStream(galleryPictures.get(position));
            if (fs != null) {
                bm = BitmapFactory.decodeFileDescriptor(fs.getFD());
                //bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                imageView.setImageBitmap(bm);
                imageView.setId(position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        */

        Picasso.with(mContext).load((galleryPictures.get(position))).fit().centerCrop().into(imageView);
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    /*
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };
    */




}