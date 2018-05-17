package com.octopus.myownsqlite.model;

import com.octopus.myownsqlite.sqlite.annotion.DbField;
import com.octopus.myownsqlite.sqlite.annotion.DbTable;

@DbTable("tb_user")
public class User {
    @DbField("user_name")
    private String username;
    @DbField("password")
    private String password;
    @DbField("age")
    private int age;

    public User(){

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int i){
        this.username = username;
        this.password = password;
        age = i;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "[username:" + this.username + " , password:" + this.password + "]\n";
    }
}
