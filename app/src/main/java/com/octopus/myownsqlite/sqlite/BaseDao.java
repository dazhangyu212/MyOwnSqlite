package com.octopus.myownsqlite.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.octopus.myownsqlite.sqlite.annotion.DbField;
import com.octopus.myownsqlite.sqlite.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dazha on 2018/4/6.
 */

public class BaseDao<T> implements IBaseDao<T> {
    private static final String TAG = "BaseDao Log";
    /**
     * 数据库引用
     */
    private SQLiteDatabase sqLiteDatabase;

    private Class<T> entityClass;

    private String tableName;

    private boolean isInit = false;

    private HashMap<String,Field> fieldMap;

    protected synchronized boolean init(Class<T> entity,SQLiteDatabase database){
        if (!isInit){
            entityClass = entity;
            sqLiteDatabase = database;
            //获取表名
            DbTable tbName = entity.getAnnotation(DbTable.class);
            tableName = tbName == null?entity.getSimpleName():tbName.value();
            if (!sqLiteDatabase.isOpen()){
                return false;
            }
            if (!autoCreateTable()){
                return false;
            }
            if (!initFieldMap()){
                return false;
            }
            isInit = true;
        }
//        initFieldMap();
        return isInit;
    }

    private boolean initFieldMap() {
        Field[] columnFields = entityClass.getDeclaredFields();
        if (columnFields == null || columnFields.length > 0){
            Log.e(TAG, "获取不到类中字段");
            return false;
        }
        //映射关系

        // 版本升级,最新版本在某个表中删除一个字段,由于数据库版本没有更新导致插入这个对象时,产生崩溃
        //2其它开发者 更改了表结构
        fieldMap = new HashMap<>();
    //查一次(空表)
        String sql = "select * from "+this.tableName+" limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
//        Field[] columnFields = entityClass.getDeclaredFields();
        for (String columnName:columnNames){
            Field resultField = null;
            for (Field field:columnFields){
                if (columnName.equals(field.getAnnotation(DbField.class).value())){
                    resultField = field;
                    break;
                }
            }
            if (resultField != null){
                fieldMap.put(columnName,resultField);
            }
        }
        cursor.close();
        return true;
    }

    private boolean getFieldMap(){
        fieldMap = new HashMap<>();
        Field[] fields = entityClass.getFields();
        if (fields == null || fields.length > 0){
            Log.e(TAG, "获取不到类中字段");
            return false;
        }
        for (Field field:fields){
            field.setAccessible(true);
            DbField dbField = field.getAnnotation(DbField.class);
            fieldMap.put(dbField == null?field.getName():dbField.value(),field);
        }
        return true;
    }

    /**
     * 创建表
     * @return
     */
    protected boolean autoCreateTable() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE IF NOT EXISTS ");
        stringBuffer.append(tableName+" ( ");
        Field[] fields = entityClass.getFields();
        if (fields != null && fields.length >0 ){
            for (Field field:fields){
                Class type = field.getType();
                if (type == String.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" TEXT, ");
                }else if (type == Double.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" DOUBLE, ");
                }else if (type == Integer.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" INTEGER, ");
                }else if (type == Long.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BIGINT, ");
                }else if (type == byte[].class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BLOB, ");
                }else {
                    Log.e(TAG, type.getClass().getName() + " don't support");
                    continue;
                }

            }

        }
        if (stringBuffer.charAt(stringBuffer.length()-1) == ','){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append(" ) ");
        try {
            this.sqLiteDatabase.execSQL(stringBuffer.toString());
        }catch (Exception e){
            return false;
        }
        Log.i(TAG,"autoCreateTable: "+stringBuffer.toString());
        return true;
    }


    @Override
    public long insert(T t) {
        long result = 0L;
        ContentValues contentValues = getValues(t);
        result = sqLiteDatabase.insert(tableName,null,contentValues);
        return result;
    }

    @Override
    public Integer delete(T where) {
        int rows = 0;
        ContentValues contentValues = getContentValues(where);
        Condition condition = new Condition(contentValues);
        rows = sqLiteDatabase.delete(tableName,condition.whereClause,condition.whereArgs);
        return rows;
    }

    @Override
    public List<T> query(T entity) {
        return query(entity,null);
    }

    @Override
    public Integer update(T entity, T where) {
        int rows = 0;
        ContentValues contentValues = getContentValues(entity);
        ContentValues whereMap = getContentValues(where);
        Condition condition = new Condition(whereMap);
        rows = sqLiteDatabase.update(tableName,contentValues,condition.getWhereClause(),condition.getWhereArgs());
        return rows;
    }

    @Override
    public List<T> query(T where, String orderBy) {
         return query(where, orderBy, null, null);
    }

    public List<T> query(T where,String orderBy,Integer page,Integer pageCount ){
        List<T> result = null;
        Cursor cursor = null;
        String limitString = null;
        if (page != null && pageCount != null){
            int startIndex = --page;
            limitString = (startIndex < 0 ? 0 : startIndex) + "," + pageCount;
        }
        try {
            if (where!= null){
                Condition condition = new Condition(getContentValues(where));
                cursor = sqLiteDatabase.query(tableName,null,condition.getWhereClause(),condition.getWhereArgs(),null, null, orderBy, limitString);
            }
            result = getResult(cursor,where);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    /**
     * 获取值
     * @param entity
     * @return
     */
    private ContentValues getContentValues(T entity) {
        ContentValues contentValues = new ContentValues();
        try{
            for (Map.Entry<String,Field> me: fieldMap.entrySet()){
                if (me.getValue().get(entity) == null){
                    continue;
                }
                contentValues.put(me.getKey(),me.getValue().get(entity).toString());
            }
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return contentValues;
    }

    public class Condition{
        private String whereClause;
        private  String[] whereArgs;

        public Condition(ContentValues whereClause) {
            ArrayList<String> list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" 1=1 ");
            Set keys = whereClause.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = (String) iterator.next();
                String value = (String) whereClause.get(key);
                if (value != null){
                    stringBuilder.append(" and "+key+" =? ");

                    list.add(value);
                }
            }
            this.whereClause = stringBuilder.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }


        public String[] getWhereArgs() {
            return whereArgs;
        }

    }

    /**
     * 将对象中的属性转成键值对
     */
    private Map<String, String> getClauseValues(T entity) throws IllegalAccessException {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            Object value = entry.getValue().get(entity);
            result.put(entry.getKey(), value == null ? "" : value.toString());
        }
        return result;
    }


    private List<T> getResult(Cursor cursor, T entity) {
        if (cursor == null){
            return null;
        }
        ArrayList list = new ArrayList();
        Object item;
        while (cursor.moveToNext()){
            try {
                item = entity.getClass().newInstance();
                Iterator<Map.Entry<String,Field>> iterator = fieldMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String,Field> entry = iterator.next();
                    String columnName = entry.getKey();
                    Field field = entry.getValue();
                    int columnIndex = cursor.getColumnIndex(columnName);
                    Class type = field.getType();
                    if (columnIndex != -1){
                        if (type == String.class){
                            field.set(item,cursor.getString(columnIndex));
                        }else if (type == Double.class){
                            field.set(item,cursor.getDouble(columnIndex));
                        }else if (type == Long.class){
                            field.set(item,cursor.getLong(columnIndex));
                        }else if (type == Integer.class){
                            field.set(item,cursor.getInt(columnIndex));
                        }else if (type == byte[].class){
                            field.set(item,cursor.getBlob(columnIndex));
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 将对象属性生成ContentValues
     * @param entity 实例
     * @return
     */
    private ContentValues getValues(T entity) {
        ContentValues contentValues = new ContentValues();
        Iterator<Map.Entry<String,Field>> iterator = fieldMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Field> fieldEntry = iterator.next();
            Field field = fieldEntry.getValue();

            String key = fieldEntry.getKey();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                Class type = field.getType();
                if (type == String.class){
                    String value = (String) object;
                    contentValues.put(key,value);
                }else if (type == Double.class){
                    Double value = (Double) object;
                    contentValues.put(key,value);
                }else if (type == Integer.class){
                    Integer value = (Integer) object;
                    contentValues.put(key,value);
                }else if (type == Long.class){
                    Long value = (Long) object;
                    contentValues.put(key,value);
                }else if (type == byte[].class){
                    Double value = (Double) object;
                    contentValues.put(key,value);
                }else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }
}
