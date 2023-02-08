package com.malka.stormy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double latitude = 55.04;
        double longitude = 82.93;

        String forecastUrl = "https://api.open-meteo.com/v1/forecast?latitude="+ latitude +"&longitude="+ longitude +"&hourly=temperature_2m";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        Log.v(TAG, response.body().string());
                    }
                }
                catch (IOException e) {
                    Log.e(TAG, "IO Exception caught: ", e);
                }
            }
        });

    }
}