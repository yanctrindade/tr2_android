package com.example.vitor.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
    }
    public void onCLickLogin(View v) {

        startActivityForResult(new Intent(this, ClienteActivity.class), 1);

    }
}
