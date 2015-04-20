package com.road_sidekiq.android.roadsidekiq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.models.Feedback;
import com.road_sidekiq.android.roadsidekiq.models.Post;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class ExperienceActivity extends ActionBarActivity {

    private final String LOG_TAG= ExperienceActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ExperienceFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.i(LOG_TAG, "onOptionsItemSelected Called");

        /*switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_settings:
                return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ExperienceFragment extends Fragment {

        private final String LOG_TAG= ExperienceFragment.class.getSimpleName();
        private String chosenId;
        private Post genPost;
        private TextView experienceDetailTextView;
        private ListView listView;
        private ArrayAdapter postListAdapter;
        private ProgressBar progressBar;
        private Button positiveButton;
        private Button negativeButton;
        HashMap<String, String> postDataParams = new HashMap<String,String>();

        public ExperienceFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(LOG_TAG, "onCreateView Called");

            View rootView = inflater.inflate(R.layout.fragment_experience_detail, container, false);

            Intent intent = getActivity().getIntent();

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                chosenId = intent.getStringExtra(Intent.EXTRA_TEXT);


                Log.i(LOG_TAG,"ChosenId : " + chosenId);

                ModelDetailApiJsonCaller caller = new ModelDetailApiJsonCaller();
                caller.execute();

//                ((TextView) rootView.findViewById(R.id.experience_title_textview)).setText(chosenId);
                experienceDetailTextView = (TextView) rootView.findViewById(R.id.experience_detail_textview);
                listView = (ListView) rootView.findViewById(R.id.feedback_listview);
                positiveButton = (Button) rootView.findViewById(R.id.positive_button);
                negativeButton = (Button) rootView.findViewById(R.id.negative_button);
                progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

                postDataParams.put("user_id",preference.getString("USERID","1"));

//                positiveButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        postDataParams.put("category", "positive");
//                        RatingsCaller caller = new RatingsCaller();
//                        caller.execute();
//
//                    }
//                });
//
//                positiveButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        postDataParams.put("category", "negative");
//                        RatingsCaller caller = new RatingsCaller();
//                        caller.execute();
//
//                    }
//                });
            }

            return rootView;
        }

        public class ModelDetailApiJsonCaller extends AsyncTask<Void, Void, Post> {

            private final String LOG_TAG= ModelDetailApiJsonCaller.class.getSimpleName();

            @Override
            protected Post doInBackground(Void... params) {
                String postJsonApi = getPostFromApi();

                try {
                    return getPostFromJson(postJsonApi);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Post post) {

                Log.v(LOG_TAG, "onPostExecute");

                super.onPostExecute(post);

                if (post != null) {
                    Log.v(LOG_TAG, "result should be added");

                    //writing the specs to the UI
                    experienceDetailTextView.setText(post.getDescription());

                    List<String> messages = new ArrayList<String>();
                    for (Feedback f : post.getFeedbacks()) {
                        messages.add(f.getMessage());
                    }

                    postListAdapter =
                            new ArrayAdapter<String>(
                                    getActivity(), // The current context (this activity)
                                    R.layout.list_item_post_feedback, // The name of the layout ID.
                                    R.id.item_post_feedback, // The ID of the textview to populate.
                                    messages);

                    progressBar.setVisibility(View.GONE);
                }

                if (postListAdapter != null) {

//                AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(postListAdapter);
//                animationAdapter.setAbsListView(listView);
                    listView.setAdapter(postListAdapter);
                }

            }

            public String getPostFromApi() {

                Log.i(LOG_TAG, "getPostFromApi called");
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String postJson = null;

                try {
                    // Construct the URL for the api query

                    final String BASE_URL = "http://10.49.236.52:3000/api/posts/" + chosenId;

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
                    postJson = buffer.toString();

                    Log.v(LOG_TAG, "Post JSON String: " + postJson);

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ");
                    e.printStackTrace();
                    // If the code didn't successfully get the weather data, there's no point in attempting
                    // to parse it.
                    postJson = null;
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

                return postJson;
            }

            private Post getPostFromJson(String postJsonStr)
                    throws JSONException {

                // These are the names of the JSON objects that need to be extracted.
                final String OWM_ID = "id";
                final String OWM_DESCRIPTION = "description";
                final String OWM_CATEGORY = "category";
                final String OWM_CREATED_AT = "created_at";
//                final String OWM_LOCATION = "location";
                final String OWM_POSITIVE = "positive";
                final String OWM_NEGATIVE = "negative";
                final String OWM_COMMENTS = "feedbacks";
                final String OWM_MESSAGE = "message";
                final String OWM_USERID = "user_id";

                //returns the feed List from the JSON List
                JSONObject postJsonOb = new JSONObject(postJsonStr);
                JSONArray feedbackArray = postJsonOb.getJSONArray(OWM_COMMENTS);

                //Create the post to be populated
                Post post = new Post();
                Feedback feedback = new Feedback();
                List<Feedback> feedbacks = new ArrayList<Feedback>();

                post.setId(chosenId);
                post.setDescription(postJsonOb.getString(OWM_DESCRIPTION));
                post.setCategory(postJsonOb.getString(OWM_CATEGORY));
                post.setCreated_at(postJsonOb.getString(OWM_CREATED_AT));
                post.setPositive(postJsonOb.getString(OWM_POSITIVE));
                post.setNegative(postJsonOb.getString(OWM_NEGATIVE));


                for (int i = 0 ; i < feedbackArray.length(); i++ ) {
                    //Get the JSON object representing the FeedBackList
                    JSONObject feedbackObj = feedbackArray.getJSONObject(i);
                    feedback.setMessage(feedbackObj.getString(OWM_MESSAGE));
                    feedback.setUserId(feedbackObj.getString(OWM_USERID));
                    feedbacks.add(feedback);
                }

                post.setFeedbacks(feedbacks);
                genPost = post;
                return post;

            }
        }

        public class RatingsCaller extends AsyncTask<Void, Void, String> {

            String response;

            private final String LOG_TAG= RatingsCaller.class.getSimpleName();

            @Override
            protected String doInBackground(Void... params) {
                try {
                    String URL = "http://10.49.236.52:3000/api/posts/"+ genPost.getId() +"/ratings/";
                    response = performPostCall(URL,postDataParams);

                    Log.i(LOG_TAG, " url : " + URL);

                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;

            }



            public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {

                URL url;
                String response = "";
                try {
                    url = new URL(requestURL);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);


                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postDataParams.get("category"));
                    writer.write(postDataParams.get("user_id"));

                    writer.flush();
                    writer.close();
                    os.close();
                    int responseCode=conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line=br.readLine()) != null) {
                            response+=line;
                        }
                    }
                    else {
                        response="";

                        throw new HttpException(responseCode+"");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return response;
            }

        }


    }
}
