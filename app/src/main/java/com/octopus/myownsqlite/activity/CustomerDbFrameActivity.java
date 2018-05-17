package com.octopus.myownsqlite.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.octopus.myownsqlite.R;
import com.octopus.myownsqlite.dao.UserDao;
import com.octopus.myownsqlite.model.User;
import com.octopus.myownsqlite.sqlite.BaseDaoFactory;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 自定义数据库框架
 */
public class CustomerDbFrameActivity extends AppCompatActivity {

    private UserDao mUserDao;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_db_frame);
        BaseDaoFactory.init(getFilesDir()+"/csdn_lqr.db");
        mUserDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        mUser = new User("CSDN_LQR", "123456", 10);
    }

    public void insert(View view) {
        Long insert = mUserDao.insert(mUser);
        Toast.makeText(getApplicationContext(), "添加了" + (insert != -1 ? 1 : 0) + "条数据", Toast.LENGTH_SHORT).show();
    }

    public void delete(View view) {
        User where = new User();
        where.setUsername("CSDN_LQR");
        Integer delete = mUserDao.delete(where);
        Toast.makeText(getApplicationContext(), "删除了" + delete + "条数据", Toast.LENGTH_SHORT).show();
    }

    public void update(View view) {
        User user = new User("LQR_CSDN", "654321", 9);

        User where = new User();
        where.setUsername("CSDN_LQR");
        Integer update = mUserDao.update(user, where);
        Toast.makeText(getApplicationContext(), "修改了" + update + "条数据", Toast.LENGTH_SHORT).show();
    }

    public void query1(View view) {
        User where = new User();
        where.setUsername("CSDN_LQR");
        where.setAge(10);
        List<User> list = mUserDao.query(where);
        int query = list != null ? list.size() :0 ;
        Toast.makeText(getApplicationContext(), "查出了" + query + "条数据", Toast.LENGTH_SHORT).show();
        for (User user : list) {
            System.out.println(user);
        }
    }

    public void query2(View view) {

        List<User> list = mUserDao.query(null, "age asc");
        int query = list == null ? 0 : list.size();
        Toast.makeText(getApplicationContext(), "查出了" + query + "条数据", Toast.LENGTH_SHORT).show();
        for (User user : list) {
            System.out.println(user);
        }
    }

    public void query3(View view) {
        List<User> list = mUserDao.query(null, "age desc");
        int query = list == null ? 0 : list.size();
        Toast.makeText(getApplicationContext(), "查出了" + query + "条数据", Toast.LENGTH_SHORT).show();
        for (User user : list) {
            System.out.println(user);
        }
    }

    public void query4(View view) {
        User where = new User();

        List<User> list = mUserDao.query(where, null, 1, 2);
        int query = list == null ? 0 : list.size();
        Toast.makeText(getApplicationContext(), "查出了" + query + "条数据", Toast.LENGTH_SHORT).show();
        for (User user : list) {
            System.out.println(user);
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, CustomerDbFrameActivity.class);
        context.startActivity(intent);
    }
}
