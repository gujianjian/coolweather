package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by joy on 2016/12/22.
 */

public class Weather {
    public Suggestion suggestion;
    public Aqi aqi;
    public Now now;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
