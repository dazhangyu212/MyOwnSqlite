package com.octopus.myownsqlite.model;

import com.octopus.myownsqlite.sqlite.annotion.DbField;
import com.octopus.myownsqlite.sqlite.annotion.DbTable;

/**
 * Created by dazha on 2018/4/6.
 */
@DbTable("tb_person")
public class Person {
    @DbField("tb_name")
    public String name;
    @DbField("tb_password")
    public Long password;
    @DbField("tb_avator")
    public byte[] avator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPassword() {
        return password;
    }

    public void setPassword(Long password) {
        this.password = password;
    }

    public byte[] getAvator() {
        return avator;
    }

    public void setAvator(byte[] avator) {
        this.avator = avator;
    }
}
