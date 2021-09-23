package com.ojtapp.divinglog.controller;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ojtapp.divinglog.util.ConversionUtil;
import com.ojtapp.divinglog.util.WeatherUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Map;

public class WeatherInfoReceiver extends AsyncTask<String, String, String[]> {
    private static final String TAG = WeatherInfoReceiver.class.getSimpleName();

    /**
     * コールバック設定用
     */
    @Nullable
    private WeatherInfoCallback weatherInfoCallback;

    @Override
    protected String[] doInBackground(String... params) {
        String cityName = params[0];
        String urlStr = ConversionUtil.getWeatherSiteURL(cityName);
        String isStr = " ";
        String weather = " ";
        String temp = " ";

        HttpURLConnection con = null;
        InputStream is = null;

        // URL先に接続し文字列データを取得する
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            is = con.getInputStream();
            isStr = is2String(is);
        } catch (IOException ex) {
            Log.e(TAG, "error = " + ex);
        } finally {
            if (con != null) {
                con.disconnect();
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Log.e(TAG, "error = " + ex);
                }
            }
        }

        // 取得した文字列データをJSONに変換し、必要な情報を取得する
        try {
            // 天気
            JSONObject rootJSON = new JSONObject(isStr);
            JSONArray weatherJSON = rootJSON.getJSONArray("weather");
            JSONObject weatherJSON0 = weatherJSON.getJSONObject(0);
            String weatherId = weatherJSON0.getString("id");
            Map<String, String> weatherMap = WeatherUtil.getWeatherMap();
            if (weatherMap.containsKey(weatherId)) {
                weather = weatherMap.get(weatherId);
            } else {
                weather = weatherJSON0.getString("main");
            }

            // 気温
            JSONObject mainJSON = rootJSON.getJSONObject("main");
            String kelvinTemp = mainJSON.getString("temp");
            double intTemp = Double.parseDouble(kelvinTemp) - 273.1;
            DecimalFormat df = new DecimalFormat("###.#");
            temp = df.format(intTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new String[]{weather, temp};
    }

    @Override
    public void onPostExecute(String[] result) {
        super.onPostExecute(result);
        String weather = result[0];
        String temp = result[1];

        if (null != weatherInfoCallback) {
            if (null != result) {
                weatherInfoCallback.onSuccess(weather, temp);
            } else {
                weatherInfoCallback.onFailure();
            }
        }
    }

    /**
     * URL先から取得したバイトデータを文字列に変換
     *
     * @param is URL先から取得したバイトデータ
     * @return バイトデータを文字列に変換したもの
     * @throws IOException
     */
    private String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        char[] b = new char[1024];
        int line;
        while (0 <= (line = reader.read(b))) {
            sb.append(b, 0, line);
        }
        return sb.toString();
    }

    /**
     * コールバック処理を設定
     *
     * @param weatherInfoCallback コールバックする内容
     */
    public void setWeatherInfoCallback(@Nullable WeatherInfoCallback weatherInfoCallback) {
        this.weatherInfoCallback = weatherInfoCallback;
    }

    /**
     * コールバック用インターフェイス
     */
    public interface WeatherInfoCallback {
        void onSuccess(String weather, String temp);

        void onFailure();
    }
}
