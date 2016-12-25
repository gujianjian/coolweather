package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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


/**
 * Created by joy on 2016/12/25.
 */

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();

        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerTime = SystemClock.elapsedRealtime() + 2000;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String url="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String imgUrl=response.body().toString();
                if(!TextUtils.isEmpty(imgUrl)){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bing_pic",null);
                    editor.apply();
                }
            }
        });
    }

    private void updateWeather()  {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather", null);
        Log.d(TAG, "updateWeather: "+weatherString);
        if(weatherString!=null){
            Weather weather = new Gson().fromJson(weatherString, Weather.class);
            String weather_id=weather.basic.weather_id;
            String url="http://guolin.tech/api/weather?cityid="+weather_id+"&key=ea0791533462468d9304948e3307963b";
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String  weather_string = response.body().string();

                    try {
                        JSONObject jsonWeather = new JSONObject(weather_string);
                        JSONArray heWeather = jsonWeather.getJSONArray("HeWeather");
                        Weather myWeather = new Gson().fromJson(heWeather.getJSONObject(0).toString(), Weather.class);
                        if(myWeather!=null&&myWeather.status.equals("ok")){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("weather",heWeather.getJSONObject(0).toString());
                            editor.apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });




        }
    }


}
