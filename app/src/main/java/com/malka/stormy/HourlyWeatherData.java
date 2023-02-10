package com.malka.stormy;

public class HourlyWeatherData {
    private String time;
    private double temperature_2m;
    private double windspeed_10m;
    private double rain;
    private double snowfall;
    private double cloudcover;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time.replace("T", ", ");
    }

    public double getTemperature_2m() {
        return temperature_2m;
    }

    public void setTemperature_2m(double temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public double getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(double windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }

    public double getRain() {
        return rain;
    }

    public void setRain(double rain) {
        this.rain = rain;
    }

    public double getSnowfall() {
        return snowfall;
    }

    public void setSnowfall(double snowfall) {
        this.snowfall = snowfall;
    }

    public double getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(double cloudcover) {
        this.cloudcover = cloudcover;
    }
}
