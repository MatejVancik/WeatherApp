package com.mv2studio.weather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WeatherActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mTextView = (TextView) findViewById(R.id.text);
    }
}
