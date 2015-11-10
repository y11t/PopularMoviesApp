package serhan.popularmoviesapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new DetailFragment())
                    .commit();
        }
    }


    public static class DetailFragment extends Fragment {

        //TODO: get Strings about movie into FrameLayout . Organize the layout files for a good detailed look.

        private String original_title;
        private ImageView movie_thumbnail;

        private String release_date;
        private String vote_average; //rating
        private String overview;    //synopsis


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_detail, container, false);
            rootView.setVisibility(rootView.VISIBLE);
            rootView.setAlpha(0);
            rootView.animate()
                    .alpha(1.0f)
                    .setDuration(800);

            String image_url;
            //deneme textview
            TextView textViews = (TextView) rootView.findViewById(R.id.movie_original_title);
            textViews.setText(MainFragment.tit);
            TextView textView2 = (TextView) rootView.findViewById(R.id.synopsis);
            textView2.setText(MainFragment.ove);
            TextView textView3 = (TextView) rootView.findViewById(R.id.release_year);
            textView3.setText(MainFragment.dat);
            TextView textView4 = (TextView) rootView.findViewById(R.id.rating);
            textView4.setText("Rating:"+ MainFragment.rat);
            movie_thumbnail = (ImageView) rootView.findViewById(R.id.movie_thumbnail);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                image_url =intent.getStringExtra(Intent.EXTRA_TEXT);
            }else{
                image_url =null;
            }

            Picasso
                    .with(getActivity())
                    .load(image_url)
                    .fit() // will explain later
                    .into(movie_thumbnail);

            return rootView;
        }

    }
}
