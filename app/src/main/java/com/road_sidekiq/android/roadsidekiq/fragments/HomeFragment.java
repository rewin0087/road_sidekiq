package com.road_sidekiq.android.roadsidekiq.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.activities.ExperienceActivity;
import com.road_sidekiq.android.roadsidekiq.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class HomeFragment extends BaseFragment {

    public static String TITLE = "HOME";
    private final String LOG_TAG= HomeFragment.class.getSimpleName();
    private ArrayAdapter postListAdapter;
    private ListView listView;

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplication();


        Log.i(LOG_TAG, "onCreateView Called");

        //Getting the Car List from API
        PostListApiJsonCaller caller = new PostListApiJsonCaller();
        caller.execute();

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        listView = (ListView) rootView.findViewById(R.id.postListView_fragment);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post chosenPost = (Post) postListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ExperienceActivity.class).putExtra(Intent.EXTRA_TEXT, chosenPost.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class PostListApiJsonCaller extends AsyncTask<Void, Void, List<Post>> {

        private final String LOG_TAG= PostListApiJsonCaller.class.getSimpleName();

        @Override
        protected List<Post> doInBackground(Void... params) {
            String postListJsonApi = getPostListFromApi();

            try {
                return getPostListFromJson(postListJsonApi);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Post> postList) {

            Log.v(LOG_TAG, "onPostExecute");

            super.onPostExecute(postList);

            if (postList != null) {
                Log.v(LOG_TAG, "result should be added");
                /*carListAdapter.clear();
                for (String carItem : postList) {
                    carListAdapter.add(carItem);
                }*/

                postListAdapter =
                        new ArrayAdapter<Post>(
                                getActivity(), // The current context (this activity)
                                R.layout.list_item_post, // The name of the layout ID.
//                                R.id.list_item_post_textview, // The ID of the textview to populate.
                                postList);
            }

            // Log.v(LOG_TAG, "checking if postList is empty");
            if (postListAdapter != null) {

                Log.v(LOG_TAG, "Setting up new animation adapter");
//                AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(postListAdapter);
//                animationAdapter.setAbsListView(listView);
                listView.setAdapter(postListAdapter);
            }
        }



        public String getPostListFromApi() {

            Log.i(LOG_TAG, "getPostListFromApi called");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String postListJson = null;

            try {

                final String BASE_URL = "http://10.49.236.52:3000/api/posts";

                Log.i(LOG_TAG, "BASE URL : " + BASE_URL);

                URL url = new URL(BASE_URL);

                // Create the request to the api, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                postListJson = buffer.toString();

                Log.v(LOG_TAG, "Post List JSON String: " + postListJson);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ");
                e.printStackTrace();
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                postListJson = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return postListJson;
        }

        private List<Post> getPostListFromJson(String postListJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_ID = "id";
            final String OWM_DESCRIPTION = "description";
            final String OWM_CATEGORY = "category";
            final String OWM_CREATED_AT = "created_at";
//            final String OWM_LOCATION = "location";

//            JSONObject postListJsonOb = new JSONObject(postListJsonStr);

            //OWM_ENTRY returns the entry array from the JSON List
            JSONArray entryJsonArry = new JSONArray(postListJsonStr);

            //Create the carList to be populated
            List<Post> postList = new ArrayList<Post>();
            for(int i = 0; i < entryJsonArry.length(); i++) {
                Post post = new Post();

                //Get the JSON object representing the CarList
                JSONObject postItemJsonOb = entryJsonArry.getJSONObject(i);

                // get the String inside the image JSON
                post.setId(postItemJsonOb.getString(OWM_ID));
                post.setDescription(postItemJsonOb.getString(OWM_DESCRIPTION));
                post.setCategory(postItemJsonOb.getString(OWM_CATEGORY));
                post.setCreated_at(postItemJsonOb.getString(OWM_CREATED_AT));
//                String location = postItemJsonOb.getString(OWM_LOCATION);

                //add the brand to the List
                postList.add(post);


            }


            return postList;

        }
    }
}
