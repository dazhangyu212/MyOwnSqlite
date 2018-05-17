package com.octopus.myownsqlite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.octopus.myownsqlite.activity.AndroidSqliteActivity;
import com.octopus.myownsqlite.activity.CustomerDbFrameActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void android_sqlite(View view) {
        AndroidSqliteActivity.launch(this);
    }

    public void customer_db_frame(View view) {
        CustomerDbFrameActivity.launch(this);
    }
}
