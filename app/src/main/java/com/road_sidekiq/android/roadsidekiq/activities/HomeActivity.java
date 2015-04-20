package com.road_sidekiq.android.roadsidekiq.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.adapters.PostListAdapter;
import com.road_sidekiq.android.roadsidekiq.models.Post;
import com.road_sidekiq.android.roadsidekiq.utilities.ConnectionDetector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class HomeActivity extends BaseActivity {

    EditText keyword;

    Button vehicleButton, locationButton;

    ListView listResult;

    PostListAdapter postListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cd = new ConnectionDetector(this);

        keyword = (EditText) findViewById(R.id.keyword);
        vehicleButton = (Button) findViewById(R.id.vehicle_button);
        locationButton = (Button) findViewById(R.id.location_button);
        if(cd.isConnectingToInternet()) {
            listResult = (ListView) findViewById(R.id.list_result);
            loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Please Connect into the Internet.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadData() {
        new HttpAsyncTask().execute(ConnectionDetector.HOST + "/api/posts");
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            try {
                JSONArray results = new JSONArray(result);
                List<Post> posts = new ArrayList();
                if(results.length() > 0) {
                    for(int i = 0; i < results.length(); i++) {
                        Post post = new Post();
                        JSONObject json = results.getJSONObject(i);

                        post.setId(json.getString("id"));
                        post.setDescription(json.getString("description"));
                        post.setCategory(json.getString("category"));
                        post.setLocation(json.getString("location"));
                        post.setLatitude(json.getString("reported_lat"));
                        post.setLongitude(json.getString("reported_long"));
                        post.setCreated_at(json.getString("created_at"));

                        posts.add(post);
                    }

                    postListAdapter = new PostListAdapter(HomeActivity.this, posts);

                    listResult.setAdapter(postListAdapter);
                    listResult.setClickable(true);
                    // set Item onClick Listener
                    listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Post post = (Post) adapterView.getItemAtPosition(i);
                            Intent intent = new Intent(getApplicationContext(), ExperienceActivity.class);
                            intent.putExtra(Intent.EXTRA_TEXT, post.getId());
                            startActivity(intent);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public void searchLocation(View v) {
        intent("location");
    }

    public void searchVehicle(View v) {
        intent("vehicle");
    }

    private void intent(String category) {
        Intent i = new Intent(getApplicationContext(), SearchResultActivity.class);
        i.putExtra("KEYWORD", keyword.getText().toString());
        i.putExtra("CATEGORY",category);
        startActivity(i);
    }
}
