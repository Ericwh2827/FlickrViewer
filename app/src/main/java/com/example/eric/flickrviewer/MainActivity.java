package com.example.eric.flickrviewer;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;

import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements ImageFragment.OnFragmentInteractionListener{

    ProgressDialog pd;

    private static JSONArray items;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<Fragment> fragments = new ArrayList<Fragment>();

    public static int width;
    public static int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new LoadImagesTask().execute("https://api.flickr.com/services/feeds/photos_public.gne?format=json");

        mPager = (ViewPager) findViewById(R.id.pager);
        final SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new LoadImagesTask().execute("https://api.flickr.com/services/feeds/photos_public.gne?format=json");

                        PagerAdapter mPagerAdapter = mPager.getAdapter();
                        mPagerAdapter.notifyDataSetChanged();

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                mPager.setCurrentItem(0);
                            }
                        });

                        if (mySwipeRefreshLayout.isRefreshing()) {
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            //Go back to earlier image
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public static JSONArray getItems() {
        return items;
    }

    public static void setItems(JSONArray items) {
        MainActivity.items = items;
    }

    class LoadImagesTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Loading Images...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));

                MainActivity.setItems(jsonObject.getJSONArray("items"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //First run only
            if(fragments.isEmpty()){
                for(int i=0; i<MainActivity.getItems().length(); i++){
                    fragments.add(ImageFragment.newInstance(Integer.toString(i)));
                }
                ViewPager mPager = (ViewPager) findViewById(R.id.pager);

                PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);

                mPager.setAdapter(mPagerAdapter);

                //Bind the circle indicator to the adapter
                CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.circles);
                circleIndicator.setViewPager(mPager);
            }
            pd.hide();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
    }
}

