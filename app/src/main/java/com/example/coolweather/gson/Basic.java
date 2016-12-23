package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 2016/12/22.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weather_id;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String update_time;
    }
}
