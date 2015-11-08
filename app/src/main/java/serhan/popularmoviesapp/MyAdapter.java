package serhan.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {//TODO:handling information and image URLS here.
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<String> imageUrls;

    public MyAdapter(Context context, ArrayList<String> imageUrls) {
        super(context, R.layout.listview_item_image, imageUrls);

        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
        }
        //TODO:Optimize the Picasso views of the app. Sort the buggy look.
        Picasso
                .with(context)
                .load(imageUrls.get(position))
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }
}