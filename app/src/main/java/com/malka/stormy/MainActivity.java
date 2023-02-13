package com.malka.stormy;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.malka.stormy.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    final double latitude = 55.04;
    final double longitude = 82.93;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForecast(latitude, longitude);
    }

    private void getForecast(double latitude, double longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);

        TextView currentTime = findViewById(R.id.currentTimeTextView);
        ImageView currentIconImageView = findViewById(R.id.currentIconImageView);
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.ENGLISH);
        currentTime.setText(dateFormat.format(currentDate));

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
                        assert response.body() != null;
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            JSONObject forecast = new JSONObject(jsonData);
                            HourlyWeatherData[] dataList = getWeatherDetails(forecast);
                            HourlyUnits units = getUnits(forecast);

                            int currentIndex;
                            for (currentIndex = 0; currentIndex < NUMBER_OF_HOURS; currentIndex++) {
                                if (currentDate.compareTo(dataList[currentIndex].getTime()) < 0) {
                                    break;
                                }
                            }

                            HourlyWeatherData displayWeatherData = new HourlyWeatherData(
                                    dataList[currentIndex].getTime(),
                                    dataList[currentIndex].getTemperature_2m(),
                                    dataList[currentIndex].getWindspeed_10m(),
                                    dataList[currentIndex].getRain(),
                                    dataList[currentIndex].getSnowfall(),
                                    dataList[currentIndex].getCloudcover()
                            );

                            HourlyUnits displayWeatherUnits = new HourlyUnits(
                                    units.getTemperature_2m(),
                                    units.getWindspeed_10m(),
                                    units.getRain(),
                                    units.getCloudcover()
                            );

                            int finalCurrentIndex = currentIndex;
                            runOnUiThread(() -> {
                                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(dataList[finalCurrentIndex].getIconId(
                                        dataList[finalCurrentIndex].getRain(),
                                        dataList[finalCurrentIndex].getSnowfall(),
                                        dataList[finalCurrentIndex].getCloudcover()));
                                currentIconImageView.setImageDrawable(drawable);
                            });

                            binding.setCurrentWeather(displayWeatherData);
                            binding.setWeatherUnits(displayWeatherUnits);
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
            data[i].setTemperature_2m((int)Double.parseDouble(Objects.requireNonNull(hourlyMap.get("temperature_2m")).getString(i)));
            data[i].setWindspeed_10m((int)Double.parseDouble(Objects.requireNonNull(hourlyMap.get("windspeed_10m")).getString(i)));
            data[i].setRain((int)Double.parseDouble(Objects.requireNonNull(hourlyMap.get("rain")).getString(i)));
            data[i].setSnowfall((int)Double.parseDouble(Objects.requireNonNull(hourlyMap.get("snowfall")).getString(i)));
            data[i].setCloudcover((int)Double.parseDouble(Objects.requireNonNull(hourlyMap.get("cloudcover")).getString(i)));
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

    public void refreshOnClick(View view) {
        getForecast(latitude, longitude);
    }
}