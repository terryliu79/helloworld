package com.test.mytest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyMainActivity extends AppCompatActivity {
    private TextView textView;//声明
    private Button button;    //声明
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        textView = (TextView)findViewById(R.id.textView);    //赋值
        button = (Button)findViewById(R.id.MyButton);          //赋值
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                textView.setText("changed");
            }
        });
    }
    public void openNewActivity(View view){
        Intent intent = new Intent(this, MySecondActivity.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = textView.getText().toString();
        intent.putExtra("msg", message);
        startActivity(intent);
    }
}
