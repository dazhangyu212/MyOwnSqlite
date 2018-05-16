package com.octopus.myownsqlite.sqlite;

import java.util.List;

/**
 * Created by dazha on 2018/4/6.
 */

public interface IBaseDao<T> {

    long insert(T t);

    List<T> query(T where);
}
