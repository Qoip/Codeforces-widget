package com.example.myapplication;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.myapplication.databinding.NewAppWidgetConfigureBinding;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * The configuration screen for the {@link NewAppWidget NewAppWidget} AppWidget.
 */
public class NewAppWidgetConfigureActivity extends Activity {

    int defColor = 3234216;
    String actColor = "#3159a8";
    private static final String PREFS_NAME = "saves";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    EditText mAppWidgetColor;
    ToggleButton togg;
    TextView checkView;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = NewAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally

            boolean fontColor = togg.isChecked();
            String widgetText = mAppWidgetText.getText().toString();
            String widgetCol = mAppWidgetColor.getText().toString();

            int fontSize = 28;
            checkView.setText(widgetText + ": 0000");
            checkView.measure(0, 0);
            Log.e("ww", String.valueOf(checkView.getMeasuredWidth()));
            while(checkView.getMeasuredWidth() >= 840){
                fontSize--;
                checkView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
                checkView.measure(0, 0);
                Log.e("ww", String.valueOf(checkView.getMeasuredWidth()));
            }
            Log.e("ww", "font size = " + fontSize);
            saveTitlePref(context, mAppWidgetId, widgetText, widgetCol, fontColor, fontSize);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            NewAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
            //NewAppWidget.lst.add(mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    View.OnClickListener col = new View.OnClickListener() {
        public void onClick(View v){
            openColorPicker();
        }
    };
    private NewAppWidgetConfigureBinding binding;

    public NewAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text, String col, boolean fc, int fs) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.putString("c_" + appWidgetId, col);
        prefs.putBoolean("fc_" + appWidgetId, fc);
        prefs.putInt("fs_" + appWidgetId, fs);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.remove("c_" + appWidgetId);
        prefs.remove("fc_" + appWidgetId);
        prefs.remove("fs_" + appWidgetId);
        prefs.remove("r_" + appWidgetId);
        prefs.remove("t_" + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.

        setResult(RESULT_CANCELED);
        binding = NewAppWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppWidgetText = binding.appwidgetName;
        mAppWidgetColor = binding.appwidgetColor;
        checkView = binding.textTry;
        togg = binding.sw;
        binding.addButton.setOnClickListener(mOnClickListener);
        binding.chColor.setOnClickListener(col);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            //NewAppWidget.lst.add(mAppWidgetId);
        }

        //NewAppWidget.lst.add(mAppWidgetId);
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        mAppWidgetText.setText(loadTitlePref(NewAppWidgetConfigureActivity.this, mAppWidgetId));
    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defColor = 16777216 + color;
                actColor = Integer.toHexString(defColor);
                while(actColor.length() < 6) actColor = "0" + actColor;
                actColor = "#" + actColor;
                mAppWidgetColor.setText(actColor);
                mAppWidgetColor.setBackgroundColor(Color.parseColor(actColor));
            }
        });
        colorPicker.show();
    }
}