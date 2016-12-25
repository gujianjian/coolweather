package com.example.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import static com.example.coolweather.R.id.bing_pic_img;
import static com.example.coolweather.R.id.srl_update_weather;

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
    private ImageView mBingImagePic;
    private SharedPreferences sp;
    public SwipeRefreshLayout mSrl_update_weather;
    public DrawerLayout mDrawerLayout;
    private Button mNav_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        sp = PreferenceManager.getDefaultSharedPreferences(this);


        mTitleText = (TextView) findViewById(R.id.title_city);
        mUpdateTimeText = (TextView) findViewById(R.id.title_update_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25 = (TextView) findViewById(R.id.pm25_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        mCarWash = (TextView) findViewById(R.id.car_wash_text);
        mComfort = (TextView) findViewById(R.id.comfort_text);

        mSrl_update_weather = (SwipeRefreshLayout) findViewById(R.id.srl_update_weather);
        mSrl_update_weather.setColorSchemeResources(R.color.colorPrimary);

        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mNav_button = (Button) findViewById(R.id.nav_button);

        mBingImagePic = (ImageView) findViewById(bing_pic_img);
        String bincPic = sp.getString("bing_pic", null);
        if (bincPic != null) {
            Glide.with(this).load(bincPic).into(mBingImagePic);
        } else {
            getImageFromServer();
        }


        final String weather_id;
        String weather = sp.getString("weather", null);
        if (weather != null) {

            Weather Weather = new Gson().fromJson(weather, Weather.class);
            weather_id = Weather.basic.weather_id;
            showWeatherInfo(Weather);
        } else {

            Intent intent = getIntent();
            weather_id = intent.getStringExtra("weather_id");
            requestWeather(weather_id);

        }
        mSrl_update_weather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weather_id);
            }
        });

        mNav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    public void requestWeather(String weather_id) {

        String address = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=ea0791533462468d9304948e3307963b";

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSrl_update_weather.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String s_weather = response.body().string();
                try {

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
                            mSrl_update_weather.setRefreshing(false);
                        }
                    });
//

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getImageFromServer() {
        String imageUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(imageUrl, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String imageSrc = response.body().string();
                sp.edit().putString("bing_pic", imageSrc).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(imageSrc).into(mBingImagePic);
                    }
                });
            }
        });
    }


    public void showWeatherInfo(Weather weather) {
        String update_time = weather.basic.update.update_time.split(" ")[1];
        //城市名字和更新时间
        mTitleText.setText(weather.basic.cityName);
        mUpdateTimeText.setText(update_time);
        //当前气温和天气概况
        mDegreeText.setText(weather.now.tmp);
        mWeatherInfoText.setText(weather.now.more.info);

        //清除所以几天天气情况
        mForecastLayout.removeAllViews();
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
        if (weather.aqi != null) {
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25.setText(weather.aqi.city.pm25);
        }

        //舒适度,运动指数和洗车指数
        mSportText.setText("运动指数:" + weather.suggestion.support.info);
        mCarWash.setText("洗车指数:" + weather.suggestion.washCar.info);
        mComfort.setText("舒适度:" + weather.suggestion.comf.info);
    }


}
