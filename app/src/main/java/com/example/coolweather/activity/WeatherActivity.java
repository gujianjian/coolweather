package com.example.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    private TextView mTitleText;
    private TextView mUpdateTimeText;
    private TextView mDegreeText;
    private TextView mWeatherInfoText;
    private TextView mAqiText;
    private TextView mPm25;
    private TextView mSportText;
    private TextView mCarWash;
    private TextView mComfort;
    private LinearLayout mForecastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        final String weather_id = intent.getStringExtra("weather_id");

        mTitleText = (TextView) findViewById(R.id.title_city);
        mUpdateTimeText = (TextView) findViewById(R.id.title_update_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25 = (TextView) findViewById(R.id.pm25_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        mCarWash = (TextView) findViewById(R.id.car_wash_text);
        mComfort = (TextView) findViewById(R.id.comfort_text);

        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        String address = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=ea0791533462468d9304948e3307963b";

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weather=sp.getString("weather",null);
        if(weather!=null){
            Weather Weather = new Gson().fromJson(weather, Weather.class);
            showWeatherInfo(Weather);
        }else {

            HttpUtil.sendOkHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String s_weather = response.body().string();
                    try {
//                    JSONObject weather_json=new JSONObject(weather);
//
//                    JSONArray array=weather_json.getJSONArray("HeWeather");
//                    JSONObject basicJson = array.getJSONObject(0).getJSONObject("basic");

                         JSONObject weather_json = new JSONObject(s_weather);
                        JSONArray array = weather_json.getJSONArray("HeWeather");
                        final JSONObject weatherJson = array.getJSONObject(0);

                        Gson gson = new Gson();
                        final Weather weather = gson.fromJson(weatherJson.toString(), Weather.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sp.edit().putString("weather", weatherJson.toString()).apply();
                                showWeatherInfo(weather);
                            }
                        });
//

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void showWeatherInfo(Weather weather){
        String update_time = weather.basic.update.update_time.split(" ")[1];
        //城市名字和更新时间
        mTitleText.setText(weather.basic.cityName);
        mUpdateTimeText.setText(update_time);
        //当前气温和天气概况
        mDegreeText.setText(weather.now.tmp);
        mWeatherInfoText.setText(weather.now.more.info);


        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, mForecastLayout, false);
            TextView date_text = (TextView) view.findViewById(R.id.date_text);
            TextView info_text = (TextView) view.findViewById(R.id.info_text);
            TextView max_text = (TextView) view.findViewById(R.id.max_text);
            TextView min_text = (TextView) view.findViewById(R.id.min_text);
            date_text.setText(forecast.date);
            info_text.setText(forecast.more.info);
            max_text.setText(forecast.tmp.max);
            min_text.setText(forecast.tmp.min);

            mForecastLayout.addView(view);
        }

        //aqi指数和pm25指数
        if(weather.aqi!=null){
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25.setText(weather.aqi.city.pm25);
        }

        //舒适度,运动指数和洗车指数
        mSportText.setText("运动指数:"+weather.suggestion.support.info);
        mCarWash.setText("洗车指数:"+weather.suggestion.washCar.info);
        mComfort.setText("舒适度:"+weather.suggestion.comf.info);
    }
}
