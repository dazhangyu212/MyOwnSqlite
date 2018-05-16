package com.octopus.myownsqlite;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.octopus.myownsqlite.model.Person;
import com.octopus.myownsqlite.sqlite.BaseDao;
import com.octopus.myownsqlite.sqlite.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {
    BaseDao<Person> baseDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseDao = BaseDaoFactory.getInstance().getBaseDao(Person.class);
    }
}
