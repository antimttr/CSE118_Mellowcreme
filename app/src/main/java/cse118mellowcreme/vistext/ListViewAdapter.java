package cse118mellowcreme.vistext;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Kristin Agcaoili on 11/24/2017.
 * Citation: SearchView Tutorial
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<String> tagList;
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> arraylist;


    public ListViewAdapter(Context context, ArrayList<String> tagList) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.tagList = new ArrayList<String>();
        this.tagList.addAll(tagList);

        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(tagList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public String getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        holder.name.setText(tagList.get(position));
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        tagList.clear();
        if (charText.length() == 0) {
            tagList.addAll(arraylist);
        } else {
            for (String tag : arraylist) {
                if (tag.toLowerCase(Locale.getDefault()).contains(charText)) {
                    tagList.add(tag);
                }
            }
        }
        notifyDataSetChanged();
    }

}
