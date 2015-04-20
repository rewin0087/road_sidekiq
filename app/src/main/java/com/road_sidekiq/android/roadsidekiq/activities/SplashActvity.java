package com.road_sidekiq.android.roadsidekiq.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.road_sidekiq.android.roadsidekiq.R;
import com.road_sidekiq.android.roadsidekiq.utilities.ConnectionDetector;
import com.road_sidekiq.android.roadsidekiq.utilities.GPSTracker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class SplashActvity extends Activity {
    final Handler handler = new Handler();

    public SharedPreferences preferences;

    public SharedPreferences.Editor editor;

    public static String LOGGEDIN = "LOGGEDIN";

    private ConnectionDetector cd;

    private HttpClient httpclient;
    private HttpPost httppost;

    private HttpResponse response;

    LinearLayout registerHolder;
    LinearLayout loginHolder;
    LinearLayout otpHolder;
    LinearLayout loginButtonHolder;
    LinearLayout otpButtonHolder;

    // Register
    EditText username, firstName, lastName, email, mobile;
    // Login
    EditText loginUser, otp;

    GPSTracker gps;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        cd = new ConnectionDetector(this);
        gps = new GPSTracker(this);

        if(!cd.isConnectingToInternet()) {
            finish();
            Toast.makeText(getApplicationContext(), "Please Connect to Internet First", Toast.LENGTH_LONG).show();
        }

        if(!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        this.preferences = getSharedPreferences("BATTERY_MONITOR", 0);
        this.editor = preferences.edit();

        if(preferences.getBoolean(LOGGEDIN, false)) {
            handler.postDelayed(go_to_home_screen, 100);
        }

        registerHolder = (LinearLayout) findViewById(R.id.register_holder);
        loginHolder = (LinearLayout) findViewById(R.id.login_holder);
        otpButtonHolder = (LinearLayout) findViewById(R.id.otp_button_holder);
        otpHolder = (LinearLayout) findViewById(R.id.otp_holder);
        loginButtonHolder = (LinearLayout) findViewById(R.id.login_button_holder);
    }

    public void goToLogin(View v) {
        registerHolder.setVisibility(View.GONE);
        loginHolder.setVisibility(View.VISIBLE);
    }

    public void goToRegister(View v) {
        loginHolder.setVisibility(View.GONE);
        registerHolder.setVisibility(View.VISIBLE);

    }

    public void registerUser(View v) {
        username = (EditText) findViewById(R.id.username);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);

        final List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("username", username.getText().toString()));
        params.add(new BasicNameValuePair("firstname", firstName.getText().toString()));
        params.add(new BasicNameValuePair("lastname", lastName.getText().toString()));
        params.add(new BasicNameValuePair("email", email.getText().toString()));
        params.add(new BasicNameValuePair("phone_number", mobile.getText().toString()));

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
                String url = ConnectionDetector.HOST + "/api/users";
                String errorMessage = "Failed To Register";
                String successMessage = "Success to register";
                SendPostRequest sendRequest = new SendPostRequest(url, params, successMessage, errorMessage, "REGISTERED");
                sendRequest.execute();
            }
        };

        handler.postDelayed(send, 300);
    }

    public void requestOTP(View v ) {
        loginUser = (EditText) findViewById(R.id.login_username);

        final List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("username", loginUser.getText().toString()));

        Runnable send =  new Runnable() {

            @Override
            public void run() {
                if (!cd.isConnectingToInternet()) {
                    cd.showDialog(getApplicationContext());
                    return;
                }

                Toast.makeText(getApplicationContext(), "Sending Request", Toast.LENGTH_LONG).show();
                String url = ConnectionDetector.HOST + "/api/users/request_one_time_password";
                String errorMessage = "Failed To Request OTP";
                String successMessage = "Successfully recieve OTP please check inbox and back again to enter your OTP.";
                SendPostRequest sendRequest = new SendPostRequest(url, params, successMessage, errorMessage, "OTP");
                sendRequest.execute();
            }
        };

        handler.postDelayed(send, 300);
    }

    public void login(View v) {
        loginUser = (EditText) findViewById(R.id.login_username);
        otp = (EditText) findViewById(R.id.one_time_password);

        final List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("username", loginUser.getText().toString()));
        params.add(new BasicNameValuePair("password", otp.getText().toString()));

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
                String url = ConnectionDetector.HOST + "/api/users/login";
                String errorMessage = "Failed To Login";
                String successMessage = "Successfully Logged in";
                SendPostRequest sendRequest = new SendPostRequest(url, params, successMessage, errorMessage, "LOGGEDIN");
                sendRequest.execute();
            }
        };

        handler.postDelayed(send, 300);
    }


    Runnable go_to_home_screen = new Runnable() {
        @Override
        public void run() {
            Intent home_screen = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(home_screen);
            finish();
        }
    };

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

                try {

                    if(requestType == "OTP") {
                        otpButtonHolder.setVisibility(View.GONE);
                        otpHolder.setVisibility(View.VISIBLE);
                        loginButtonHolder.setVisibility(View.VISIBLE);
                    } else {

                        if(requestType == "LOGGEDIN" || requestType == "REGISTERED") {
                            editor.putBoolean(LOGGEDIN, true);
                            editor.putString("USERID", finalResult.getString("id"));
                            editor.commit();
                        }

                        handler.postDelayed(go_to_home_screen, 2000);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                editor.putBoolean(LOGGEDIN, false);
                editor.commit();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
