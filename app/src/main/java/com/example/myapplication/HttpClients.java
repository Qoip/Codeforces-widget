package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.core.widget.TextViewCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HttpClients extends AsyncTask<String, String, String> {
    RemoteViews views = new RemoteViews(NewAppWidget.packageName, R.layout.new_app_widget);

    protected String doInBackground(String... params) {
        Log.i("work", "**** Request http");
        views.setFloat(R.id.appwidget_text, "setTextSize", 28);
        views.setTextViewText(R.id.appwidget_text, "Refreshing...");
        NewAppWidget.awm.updateAppWidget(NewAppWidget.id, views);

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String exep;
        Log.e("ww", "1");
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.e("ww", "2");

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            Log.e("ww", "3");
            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");

            }
            Log.e("ww", "4");
            return buffer.toString();

        } catch (IOException e) {
            Log.e("HttpClients", "a "+e.getMessage());
            exep = e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e("HttpClients", "b "+e.getMessage());
            }
        }
        Log.e("ww", "5");
        return "e"+exep;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e("ww", "6");
        SharedPreferences sharedPref = NewAppWidget.cnt.getSharedPreferences("saves", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String cl = sharedPref.getString("c_" + NewAppWidget.id, "#FF81D4FA");
        //Log.e("ww", cl);
        views.setInt(R.id.imageBG, "setBackgroundColor", Color.parseColor(cl));

        boolean fontCol = sharedPref.getBoolean("fc_" + NewAppWidget.id, false);
        if(fontCol){
            views.setInt(R.id.date, "setTextColor", Color.parseColor("#2c2d30"));
            views.setInt(R.id.appwidget_text, "setTextColor", Color.parseColor("#2c2d30"));
        }
        else{
            views.setInt(R.id.date, "setTextColor", Color.parseColor("#FFFFFF"));
            views.setInt(R.id.appwidget_text, "setTextColor", Color.parseColor("#FFFFFF"));
        }

        if(s.charAt(0) == 'e') s = "Net error";
        else {
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray jArray = obj.getJSONArray("result");
                JSONObject obj2 = jArray.getJSONObject(0);
                s = obj2.getString("rating");
            } catch (JSONException e) {
                Log.e("NewAppWidget", "err " + e.getMessage());
                s = "Error";
                // not a json
            }
        }
        if(s.length() < 5){
            Date currentDate = new Date(); // date change
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String dateText = dateFormat.format(currentDate);
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String timeText = timeFormat.format(currentDate);
            String str = dateText + "  " + timeText;
            views.setTextViewText(R.id.date, str);

            String name = NewAppWidgetConfigureActivity.loadTitlePref(NewAppWidget.cnt, NewAppWidget.id);
            s = name + ": " + s;
            editor.putString("r_" + NewAppWidget.id, s); //save s
            editor.putString("t_" + NewAppWidget.id, str); //save time
            editor.apply();
            Log.i("work", "**** Get ok");
        }
        else{
            views.setTextViewText(R.id.appwidget_text, s);
            NewAppWidget.awm.updateAppWidget(NewAppWidget.id, views);
            s = sharedPref.getString("r_" + NewAppWidget.id, "No saved"); // get save

            String str = sharedPref.getString("t_" + NewAppWidget.id, "No updates");
            views.setTextViewText(R.id.date, str);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("err", "Sleep error");
            }
            Log.w("work", "**** Get error");
        }
        Log.e("ww", s);

        int fs = sharedPref.getInt("fs_" + NewAppWidget.id, 28);
        //views.setFloat(R.id.appwidget_text, "setTextSize", fs);
        views.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_DIP, fs);
        views.setTextViewText(R.id.appwidget_text, s);
        NewAppWidget.awm.updateAppWidget(NewAppWidget.id, views);

        super.onPostExecute(s);
    }
}

