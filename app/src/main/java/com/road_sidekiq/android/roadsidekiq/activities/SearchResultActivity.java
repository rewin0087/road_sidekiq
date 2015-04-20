package com.road_sidekiq.android.roadsidekiq.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.adapters.PostListAdapter;
import com.road_sidekiq.android.roadsidekiq.models.Post;
import com.road_sidekiq.android.roadsidekiq.utilities.ConnectionDetector;
import com.road_sidekiq.android.roadsidekiq.utilities.GPSTracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class SearchResultActivity extends BaseActivity {

    ListView listResult;

    PostListAdapter postListAdapter;

    String category;
    String keyword;

    RelativeLayout submitReportForm;

    LinearLayout vechicleReport, locationReport;

    EditText formLocationDescription, formVehicleDescription, formVehicle;

    private HttpClient httpclient;

    private HttpPost httppost;

    private HttpResponse response;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle extras = getIntent().getExtras();

        submitReportForm = (RelativeLayout) findViewById(R.id.submit_report_form);
        vechicleReport = (LinearLayout) findViewById(R.id.vechicle_report);
        locationReport = (LinearLayout) findViewById(R.id.location_report);

        formLocationDescription = (EditText) findViewById(R.id.form_location_description);
        formVehicleDescription = (EditText) findViewById(R.id.form_vehicle_description);
        formVehicle = (EditText) findViewById(R.id.form_vehicle);

        cd = new ConnectionDetector(this);
        gps = new GPSTracker(this);
        this.preferences = getSharedPreferences("BATTERY_MONITOR", 0);

        if(!cd.isConnectingToInternet()) {
            finish();
            Toast.makeText(getApplicationContext(), "Please Connect to Internet First", Toast.LENGTH_LONG).show();
        }

        if(!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        if(extras != null) {
            category = extras.getString("CATEGORY");
            keyword = extras.getString("KEYWORD");
            TextView keywordText = (TextView) findViewById(R.id.keyword);
            TextView categoryText = (TextView) findViewById(R.id.category);

            categoryText.setText(category.toUpperCase() + ":");
            keywordText.setText(keyword);
            listResult = (ListView) findViewById(R.id.list_result);
            loadData();
        } else {
            Toast.makeText(this, "Unable to Start.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void showAddReportLocation(View v) {
        submitReportForm.setVisibility(View.VISIBLE);
        locationReport.setVisibility(View.VISIBLE);
    }

    public void cancelReport(View v) {
        submitReportForm.setVisibility(View.GONE);
        locationReport.setVisibility(View.GONE);
        vechicleReport.setVisibility(View.GONE);
    }
    public void showAddReportVehicle(View v) {
        submitReportForm.setVisibility(View.VISIBLE);
        vechicleReport.setVisibility(View.VISIBLE);
    }

    public void sendVehicleReport(View v) {
        Log.d("VEHICLE REPORT", "1");
        final List<NameValuePair> params = new ArrayList();
        String userId = preferences.getString("USERID", "");

        params.add(new BasicNameValuePair("user_id",userId));
        params.add(new BasicNameValuePair("description", formVehicleDescription.getText().toString()));
        params.add(new BasicNameValuePair("plate_number", formVehicle.getText().toString()));
        params.add(new BasicNameValuePair("category", "vehicle"));

        if(gps.canGetLocation()) {
            params.add(new BasicNameValuePair("latitude", Double.toString(gps.getLatitude())));
            params.add(new BasicNameValuePair("longitude", Double.toString(gps.getLongitude())));
        }

        Runnable send = new Runnable() {

            @Override
            public void run() {
                if (!cd.isConnectingToInternet()) {
                    cd.showDialog(getApplicationContext());
                    return;
                }

                Toast.makeText(getApplicationContext(), "Sending Request", Toast.LENGTH_LONG).show();
                String url = ConnectionDetector.HOST + "/api/posts";
                String errorMessage = "Failed To Submit Report";
                String successMessage = "Successfully Submitted Report";
                SendPostRequest sendRequest = new SendPostRequest(url, params, successMessage, errorMessage, "REGISTERED");
                sendRequest.execute();
            }
        };

        handler.postDelayed(send, 300);
    }

    public void sendLocationReport(View v) {
        Log.d("LOCATION REPORT", "1");
        final List<NameValuePair> params = new ArrayList();
        String userId = preferences.getString("USERID", "");

        params.add(new BasicNameValuePair("user_id",userId));
        params.add(new BasicNameValuePair("description", formLocationDescription.getText().toString()));
        params.add(new BasicNameValuePair("category", "location"));

        if(gps.canGetLocation()) {
            params.add(new BasicNameValuePair("latitude", Double.toString(gps.getLatitude())));
            params.add(new BasicNameValuePair("longitude", Double.toString(gps.getLongitude())));
        }

        Runnable send = new Runnable() {

            @Override
            public void run() {
                if (!cd.isConnectingToInternet()) {
                    cd.showDialog(getApplicationContext());
                    return;
                }

                Toast.makeText(getApplicationContext(), "Sending Request", Toast.LENGTH_LONG).show();
                String url = ConnectionDetector.HOST + "/api/posts";
                String errorMessage = "Failed To Submit Report";
                String successMessage = "Successfully Submitted Report";
                SendPostRequest sendRequest = new SendPostRequest(url, params, successMessage, errorMessage, "REGISTERED");
                sendRequest.execute();
            }
        };

        handler.postDelayed(send, 300);
    }

    private void loadData() {
        Log.d("CATEGORY", category);
        Log.d("KEYWORD", keyword);
        if(category.matches("location")) {
            new HttpAsyncTask().execute(ConnectionDetector.HOST + "/api/posts/search?location=" + keyword);
        } else if(category.matches("vehicle")) {
            new HttpAsyncTask().execute(ConnectionDetector.HOST + "/api/posts/search?plate_number=" + keyword);
        }
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

                    postListAdapter = new PostListAdapter(SearchResultActivity.this, posts);

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

    public class SendPostRequest
            extends AsyncTask<Void, Void, Void> {
        public List<NameValuePair> params;

        public String url;

        public String successMessage;

        public String errorMessage;

        public String requestType;

        JSONObject finalResult;

        public SendPostRequest(String url, List<NameValuePair> params, String successMessage, String errorMessage, String requestType) {
            this.params = params;
            this.url = url;
            this.successMessage = successMessage;
            this.errorMessage = errorMessage;
            this.requestType = requestType;
        }

        protected Void doInBackground(Void... paramVarArgs) {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(url);

            try {
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httppost.setEntity(new UrlEncodedFormEntity(params));
                httppost.setHeader("Accept-Charset", "utf-8");

                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                Log.e("Params", response.getParams().toString());
                Log.e("STATUS", Integer.toString(response.getStatusLine().getStatusCode()));


                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));

                String jsonString = reader.readLine();
                JSONTokener tokener = new JSONTokener(jsonString);

                finalResult = new JSONObject(tokener);

                return null;
            } catch (IOException localIOException) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void paramVoid) {
            if ((response != null) && (response.getStatusLine().getStatusCode() == 200)) {
                Toast.makeText(getApplicationContext(), successMessage, Toast.LENGTH_SHORT).show();

                submitReportForm.setVisibility(View.GONE);
                locationReport.setVisibility(View.GONE);
                vechicleReport.setVisibility(View.GONE);

            } else {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
