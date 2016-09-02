package com.test.mytest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySecondActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_second);
        textView=(TextView)findViewById(R.id.textView3);
        Intent intent = getIntent();
        String msg=intent.getStringExtra("msg");
        textView.setText(msg );
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("new one");

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }
}
