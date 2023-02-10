package com.malka.stormy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_OF_HOURS = 168;
    private final String[] PARAMETERS = {"latitude", "longitude", "hourly"};
    private final String[] HOURLY_VALUES = {"temperature_2m", "windspeed_10m", "rain", "snowfall", "cloudcover"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double latitude = 55.04;
        double longitude = 82.93;

        String BASE_URL = "https://api.open-meteo.com/v1/forecast";
        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(PARAMETERS[0], Double.toString(latitude))
                .appendQueryParameter(PARAMETERS[1], Double.toString(longitude))
                .appendQueryParameter(PARAMETERS[2], String.join(",", HOURLY_VALUES))
                .build();

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(uri.toString().replace("%2C", ","))
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject forecast = new JSONObject(jsonData);
                            HourlyWeatherData[] dataList = getWeatherDetails(forecast);
                            HourlyUnits units = getUnits(forecast);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });
        }
    }

    private HourlyUnits getUnits(JSONObject forecast) throws JSONException {
        HourlyUnits data = new HourlyUnits();

        JSONObject hourly_units = forecast.getJSONObject("hourly_units");
        data.setTemperature_2m(Objects.requireNonNull(hourly_units.get("temperature_2m").toString()));
        data.setWindspeed_10m(Objects.requireNonNull(hourly_units.get("windspeed_10m")).toString());
        data.setRain(Objects.requireNonNull(hourly_units.get("rain")).toString());
        data.setSnowfall(Objects.requireNonNull(hourly_units.get("snowfall")).toString());
        data.setCloudcover(Objects.requireNonNull(hourly_units.get("cloudcover")).toString());

        return data;
    }

    private HourlyWeatherData[] getWeatherDetails(JSONObject forecast) throws JSONException {
        HourlyWeatherData[] data = new HourlyWeatherData[NUMBER_OF_HOURS];

        JSONObject hourly = forecast.getJSONObject("hourly");
        Map<String, JSONArray> hourlyMap = new HashMap<>();

        hourlyMap.put("time", hourly.getJSONArray("time"));
        for (String hourlyValue : HOURLY_VALUES) {
            hourlyMap.put(hourlyValue, hourly.getJSONArray(hourlyValue));
        }

        for (int i = 0; i < NUMBER_OF_HOURS; i++) {
            data[i] = new HourlyWeatherData();
            data[i].setTime(Objects.requireNonNull(hourlyMap.get("time")).getString(i));
            data[i].setTemperature_2m(Double.parseDouble(Objects.requireNonNull(hourlyMap.get("temperature_2m")).getString(i)));
            data[i].setWindspeed_10m(Double.parseDouble(Objects.requireNonNull(hourlyMap.get("windspeed_10m")).getString(i)));
            data[i].setRain(Double.parseDouble(Objects.requireNonNull(hourlyMap.get("rain")).getString(i)));
            data[i].setSnowfall(Double.parseDouble(Objects.requireNonNull(hourlyMap.get("snowfall")).getString(i)));
            data[i].setCloudcover(Double.parseDouble(Objects.requireNonNull(hourlyMap.get("cloudcover")).getString(i)));
        }

        return  data;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}