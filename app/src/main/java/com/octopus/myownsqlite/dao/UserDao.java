package com.octopus.myownsqlite.dao;

import com.octopus.myownsqlite.model.User;
import com.octopus.myownsqlite.sqlite.BaseDao;

public class UserDao extends BaseDao<User> {
    //    @Override
    //    protected boolean createTable(SQLiteDatabase database) {
    //        database.execSQL("create table if not exists t_user(tb_name varchar(30),tb_password varchar(10))");
    //        return super.createTable(database);
    //    }
}
