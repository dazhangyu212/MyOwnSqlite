package com.octopus.myownsqlite.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Created by dazha on 2018/4/15.
 */

public class BaseDaoFactory {

    private static String sqliteDatabasePath;
    SQLiteDatabase sqLiteDatabase;
    public static BaseDaoFactory getInstance(){
        return Instance.INSTANCE;
    }

    private static class Instance {
        public static BaseDaoFactory INSTANCE = new BaseDaoFactory();
    }
    /**
     * 初始化数据库位置
     * @param dbPath
     */
    public static void init(String dbPath){
        sqliteDatabasePath = dbPath;
    }

    private BaseDaoFactory() {
        if (TextUtils.isEmpty(sqliteDatabasePath)){
            throw new RuntimeException("数据库路径不可为空");
        }
//        sqliteDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/testsqlite/myown.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
//        BaseDao<Person> baseDao = new BaseDao<>();
//        baseDao.init(Person.class,sqLiteDatabase);
    }

    public <T extends BaseDao<M>,M>T getBaseDao(Class<T> clazz, Class<M> entity){
        //只处理通用逻辑
        T baseDao = null;
        try {
           baseDao = clazz.newInstance();
           baseDao.init(entity,sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }finally {
            return baseDao;
        }

    }
}
