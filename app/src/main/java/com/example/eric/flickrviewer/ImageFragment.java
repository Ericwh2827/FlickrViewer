package com.example.eric.flickrviewer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class ImageFragment extends Fragment {
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private OnFragmentInteractionListener mListener;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View[] v = {inflater.inflate(R.layout.fragment_image, container, false)};

        JSONObject item = null;
        try {
            String message = getArguments().getString(EXTRA_MESSAGE);
            item = (JSONObject) MainActivity.getItems().get(Integer.valueOf(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get the title
        String title = null;
        try {
            title = (String) item.get("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView messageTextView = (TextView) v[0].findViewById(R.id.textView2);
        messageTextView.setText(title);


        //Get url from media object
        String url = null;
        try {
            url = item.getJSONObject("media").getString("m");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView imageView = (ImageView) v[0].findViewById(R.id.imageView2);

        new DownloadImageTask(imageView).execute(url);


        return v[0];
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Fragment newInstance(String s) {
        ImageFragment f = new ImageFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, s);
        f.setArguments(bdl);
        return f;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
