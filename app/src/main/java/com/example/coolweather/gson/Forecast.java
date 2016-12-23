package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 2016/12/22.
 */

public class Forecast {

    public String date;
    @SerializedName("cond")
    public More more;

    public Tmp tmp;

    public class More {
        @SerializedName("txt_d")
        public String info;
    }

    public class Tmp {
        public String max;
        public String min;
    }
}
