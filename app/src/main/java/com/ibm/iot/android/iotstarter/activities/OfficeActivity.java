package com.ibm.iot.android.iotstarter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.iot.android.iotstarter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OfficeActivity extends Activity {

    private boolean present = false;
    private String bleString;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
        initializeButtons();
        status = (TextView) findViewById(R.id.status);

        // Start handler
        handler.postDelayed(runnable, 10);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handleUpdate();
            handler.postDelayed(this, 1000);
        }
    };

    private void handleUpdate()
    {
        present = false;
        checkBLEPresent();
    }

    private String readBLEJSONFile(){
        // Method obtained through https://developer.android.com/training/volley/simple
        // on April 30 2019

        //final TextView mTextView = (TextView) findViewById(R.id.textView);
        // ...
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://c16cf5d8-caef-4b56-8e05-61909e37ba9a-bluemix.cloudant.com/present/_all_docs?include_docs=true";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.toString());
                        bleString = response.toString();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText(error.toString());
            }
        });

        // Add the request to the RequestQueue. 
        queue.add(stringRequest);
        return bleString;
    }

    private void checkBLEPresent() {

        String bleIDString = readBLEJSONFile();

        try {
            JSONObject reader = new JSONObject(bleIDString);

            JSONArray rows = reader.getJSONArray("rows");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject information = rows.getJSONObject(i).getJSONObject("doc");
                present = information.getBoolean("present");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        if (present) {
            status.setText("In Office");
        } else {
            status.setText("Not In Office");
        }
    }

    private void initializeButtons() {
        Button logout = (Button) this.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    private void handleLogout()
    {
        finish();
        Intent intent = new Intent(this.getApplicationContext(), MainPagerActivity.class);
        startActivity(intent);
    }

}
