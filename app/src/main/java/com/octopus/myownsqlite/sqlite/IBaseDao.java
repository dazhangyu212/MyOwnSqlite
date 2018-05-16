package com.octopus.myownsqlite.sqlite;

import java.util.List;

/**
 * Created by dazha on 2018/4/6.
 */

public interface IBaseDao<T> {

    long insert(T t);

    List<T> query(T where);

    Integer delete(T where);

    Integer update(T entitiy, T where);

    List<T> query(T where, String orderBy);

    List<T> query(T where, String orderBy, Integer page, Integer pageCount);
}
