package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 2016/12/22.
 */

public class Suggestion {
    @SerializedName("cw")
    public WashCar washCar;
    @SerializedName("air")
    public Support support;
    public Comf comf;

    public class Comf {
        @SerializedName("txt")
        public String info;
    }

    public class Support {
        @SerializedName("txt")
        public String info;
    }

    public class WashCar {
        @SerializedName("txt")
        public String info;
    }
}
