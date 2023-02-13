package com.malka.stormy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourlyWeatherData {
    private Date time;
    private int temperature_2m;
    private int windspeed_10m;
    private int rain;
    private int snowfall;
    private int cloudcover;

    public HourlyWeatherData() {

    }

    public HourlyWeatherData(Date time, int temperature_2m, int windspeed_10m,
                             int rain, int snowfall, int cloudcover) {
        this.time = time;
        this.temperature_2m = temperature_2m;
        this.windspeed_10m = windspeed_10m;
        this.rain = rain;
        this.snowfall = snowfall;
        this.cloudcover = cloudcover;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(String time) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
        try {
            this.time = parser.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTemperature_2m() {
        return temperature_2m;
    }

    public void setTemperature_2m(int temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public int getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(int windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }

    public int getRain() {
        return rain;
    }

    public void setRain(int rain) {
        this.rain = rain;
    }

    public int getSnowfall() {
        return snowfall * 10;
    }

    public void setSnowfall(int snowfall) {
        this.snowfall = snowfall;
    }

    public int getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(int cloudcover) {
        this.cloudcover = cloudcover;
    }

    public int getIconId(double rain, double snowfall, double cloudcover) {
        if (rain > 0 && snowfall > 0) return R.drawable.sleet;
        else if (rain > 0) return R.drawable.rain;
        else if (snowfall > 0) return R.drawable.snow;
        if (cloudcover > 0) return R.drawable.cloudy;
        else return R.drawable.clear;
    }
}
