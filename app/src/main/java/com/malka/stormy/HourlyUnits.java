package com.malka.stormy;

public class HourlyUnits {
    private String temperature_2m;
    private String windspeed_10m;
    private String rain;
    private String cloudcover;

    public HourlyUnits() {

    }

    public HourlyUnits(String temperature_2m, String windspeed_10m, String rain, String cloudcover) {
        this.temperature_2m = temperature_2m;
        this.windspeed_10m = windspeed_10m;
        this.rain = rain;
        this.cloudcover = cloudcover;
    }

    public String getTemperature_2m() {
        return temperature_2m;
    }

    public void setTemperature_2m(String temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public String getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(String windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(String cloudcover) {
        this.cloudcover = cloudcover;
    }
}
