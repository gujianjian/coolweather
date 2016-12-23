package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by joy on 2016/12/22.
 */

public class Now {
    public String tmp;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
