package vijay.com.volleygson;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnGET;
    ArrayList<UserList.UserDataList> mUserDataList = new ArrayList<>();
    String BASE_URL = "https://reqres.in";
    int numberOfRequestsCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGET = findViewById(R.id.btnGET);
        btnGET.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGET:
                GETStringAndJSONRequest("2", "4");
                break;

        }
    }

    private void GETStringAndJSONRequest(String page_1, String page_2) {
        mUserDataList.clear();
        numberOfRequestsCompleted = 0;
        VolleyLog.DEBUG = true;
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        String uri_page_one = String.format(BASE_URL + "/api/users?page=%1$s", page_1);
        Log.e("uri_page_one", uri_page_one);
        final String uri_page_two = String.format(BASE_URL + "/api/users?page=%1$s", page_2);
        Log.e("uri_page_two", uri_page_two);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri_page_one, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                VolleyLog.d(response);
                Log.e("response", response);
                GsonBuilder builder = new GsonBuilder();
                Gson mGson = builder.create();
                UserList userList = mGson.fromJson(response, UserList.class);
                Log.e("API123", userList.page + "");
                mUserDataList.addAll(userList.userDataList);
                ++numberOfRequestsCompleted;

            }
        }, errorListener) {

            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

        };

        queue.add(stringRequest);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(uri_page_two, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                VolleyLog.wtf(response.toString(), "utf-8");
                GsonBuilder builder = new GsonBuilder();
                Gson mGson = builder.create();

                UserList userList = mGson.fromJson(response.toString(), UserList.class);
                mUserDataList.addAll(userList.userDataList);
                ++numberOfRequestsCompleted;

            }
        }, errorListener) {

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        queue.add(jsonObjectRequest);


        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {

            @Override
            public void onRequestFinished(Request<Object> request) {
                try {
                    if (request.getCacheEntry() != null) {
                        String cacheValue = new String(request.getCacheEntry().data, "UTF-8");
                        Log.e("API123", request.getCacheKey() + " " + cacheValue);

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (numberOfRequestsCompleted == 2) {
                    numberOfRequestsCompleted = 0;
                    startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class).putExtra("users", mUserDataList));
                }
            }
        });

    }

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError) {
                Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

}
